package ru.civwars.town;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import ru.civwars.CivLogger;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.building.BuildContext;
import ru.civwars.building.BuildingPlacer;
import ru.civwars.building.BuildError;
import ru.civwars.building.Building;
import ru.civwars.building.types.StructureData;
import ru.civwars.building.types.TownHallData;
import ru.civwars.building.instance.Structure;
import ru.civwars.civ.CivDefaultRanks;
import ru.civwars.civ.Civilization;
import ru.civwars.civ.CivManager;
import ru.civwars.database.Database;
import ru.civwars.entity.player.PlayerCache;
import ru.civwars.schematic.Schematic;
import ru.civwars.util.BlockPos;
import ru.civwars.util.ChunkCoord;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.Result;
import ru.civwars.world.CivWorld;
import ru.databaseapi.database.DatabaseRow;
import ru.civwars.building.BuildingManager;
import ru.civwars.chat.requests.ChatRequest;
import ru.civwars.chat.requests.ChatRequestManager;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.handlers.CivHandler;
import ru.civwars.handlers.TownHandler;
import ru.civwars.i18n.I18n;
import ru.civwars.petitions.PetitionManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class Town {

    public static final int RANK_MASTER = 0;
    public static final int RANK_OFFCIER = 1;
    public static final int RANK_MEMBER = 2;

    public final Random random = new Random();

    /* Идентификатор город. */
    private final UUID id;

    /* Имя город. */
    private String name;

    /* Цивилизация, которой принадлежит этот город. */
    private Civilization civ;

    /* Цивилизация, которой принадлежал город до завоевания. */
    private Civilization motherCiv;

    /* Идентификатор лидера. */
    private UUID leaderId = null;

    /* Карта участников, проиндексированных по идентификатору игрока. */
    private final ConcurrentHashMap<UUID, TownMember> members = new ConcurrentHashMap<>();

    /* Карта рангов, проиндексированных по идентификатору ранга. */
    private final ConcurrentHashMap<Integer, TownRank> ranks = new ConcurrentHashMap<>();

    /* Золото города. */
    private long gold = 0;

    /* Время создания города. */
    private long createdTime = 0;

    /* Карта структур, проиндексированных по идентификатору структуры. */
    private final ConcurrentHashMap<UUID, Building> buildings = new ConcurrentHashMap<>();

    /* Культурный чанки города. */
    private final ConcurrentHashMap<ChunkCoord, CultureChunk> cultureChunks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Town> borderTowns = new ConcurrentHashMap<>();

    /**
     * Создает новый город с заданным идентификатором.
     *
     * Позже, объект должен быть инициализирован вызовом
     * {@link #create(KPlayer, Civilization, String)} или
     * {@link #loadFromDB(DatabaseRow)}.
     *
     * @param id идентификатор города.
     */
    public Town(@NotNull UUID id) {
        this.id = id;
    }

    /**
     * Инициализирует этот город и сохраняет в базу данных.
     *
     * @param leader лидер города.
     * @param civ цивилизация.
     * @param name имя города.
     * @param newCiv
     * @param type тип структуры.
     * @param world мир, в котором создается город.
     * @param pos координата блока, где расположен дом города.
     * @param direction направление взгляда.
     * @param schematic схематик.
     * @return {@code true}, если город сохранен. Иначе {@code false}.
     */
    public boolean create(@NotNull KPlayer leader, @NotNull Civilization civ, @NotNull String name, boolean newCiv, @NotNull TownHallData type, @NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull Schematic schematic) {
        if (TownManager.getTown(name) != null || (leader.getTown() != null && leader.getCiv() != civ)) {
            return false;
        }

        CivLogger.log(Level.INFO, "Creating town [{0}] for civilization {1} ({2}) with leader {3} ({4})", new Object[]{name, civ.getName(), civ.getId(),
            leader.getName(), leader.getObjectId()});

        long now = System.currentTimeMillis();
        this.name = name;
        this.civ = civ;
        this.motherCiv = civ;
        this.leaderId = leader.getObjectId();

        this.createdTime = now;

        boolean ok = Database.getConnection().execute("INSERT INTO towns (TownId,Name,CivId,MotherCivId,LeaderId,Gold,CreatedDate) VALUES(?,?,?,?,?,?,?)",
                this.getId(),
                this.getName(),
                this.getCiv().getId(),
                this.getMotherCiv().getId(),
                this.getLeaderId(),
                this.getGold(),
                this.getCreatedTime()
        );

        if (ok) {
            int civRankId = newCiv ? Civilization.RANK_MASTER : Civilization.RANK_MEMBER;
            Town oldTown = leader.getTown();
            if (oldTown != null) {
                TownMember oldMember = oldTown.getMember(leader.getObjectId());
                if (oldMember != null) {
                    civRankId = oldMember.getCivRankId();
                }
                oldTown.removeMember(leader.getObjectId(), false);
            }

            this.createDefaultRanks();
            ok = this.addMember(leader, RANK_MASTER, civRankId);
            if (ok) {
                ok = this.build(type, world, pos, direction, schematic);
            }
        }

        return ok;
    }

    private void createDefaultRanks() {
        Database.getConnection().execute("DELETE FROM town_ranks WHERE TownId=?", this.getId());
        this.createRank("Master", RankRights.RIGHTS_TOWN_MASTER);
        this.createRank("Offcier", RankRights.RIGHTS_TOWN_OFFICER);
        this.createRank("Member", RankRights.RIGHTS_TOWN_MEMBER);
    }

    /**
     * Распускает город и удаляет все связанные данные из базы данных.
     */
    public void disband() {
        Collection<TownMember> members = this.members.values();
        for (TownMember member : members) {
            this.removeMember(member.getId(), true);
        }

        Database.getConnection().asyncExecute("DELETE FROM towns WHERE TownId=?", this.getId());

        TownManager.removeTown(this);
    }

    /**
     * Получает идентификатор города.
     *
     * @return идентификатор города.
     */
    @NotNull
    public final UUID getId() {
        return this.id;
    }

    /**
     * Устанавливает имя города.
     *
     * @param name имя.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Получает имя города.
     *
     * @return имя города.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * Устанавливает цивилизацию, которой принадлежит город.
     *
     * @param civ цивилизация.
     */
    public void setCiv(@NotNull Civilization civ) {
        this.civ = civ;
    }

    /**
     * Получает цивилизацию, которой принадлежит город.
     *
     * @return цивилизация.
     */
    @NotNull
    public Civilization getCiv() {
        return this.civ;
    }

    /**
     * Устанавливает цивилизацию, которой принадлежал город до завоевания.
     *
     * @param motherCiv цивилизация.
     */
    public void setMotherCiv(@NotNull Civilization motherCiv) {
        this.motherCiv = motherCiv;
    }

    /**
     * Получает цивилизацию, которой принадлежал город до завоевания.
     *
     * @return цивилизация.
     */
    @NotNull
    public Civilization getMotherCiv() {
        return this.motherCiv;
    }

    /**
     * Устанавливает участника новым лидером города.
     *
     * @param member новый лидер.
     */
    public void setLeaderId(@NotNull TownMember member) {
        this.leaderId = member.getId();
        Database.getConnection().asyncExecute("UPDATE towns SET LeaderId=? WHERE TownId=?", this.leaderId, this.getId());
    }

    /**
     * Получает идентификатор лидера городаа.
     *
     * @return идентификатор лидера или {@code null}, если не задан.
     */
    @Nullable
    public UUID getLeaderId() {
        return this.leaderId;
    }

    /**
     * Определяет, является ли игрок лидером городаа.
     *
     * @param player игрок.
     * @return {@code true}, если игрок является лидером. Иначе {@code false}.
     */
    public boolean isLeader(@NotNull KPlayer player) {
        if (Objects.equal(this.leaderId, player.getObjectId())) {
            return true;
        }
        return false;
    }

    /**
     * Добавляет участника.
     *
     * @param player
     * @param rankId
     * @param civRankId
     * @return {@code true}, если участник добавлен. Иначе {@code false}.
     */
    public boolean addMember(@NotNull KPlayer player, int rankId, int civRankId) {
        if (player.getTown() != null) {
            return false;
        }

        if (this.members.containsKey(player.getObjectId())) {
            CivLogger.log(Level.SEVERE, "Tried to add {0} to town {1}. Member already exists.", player.getObjectId(), this.getName());
            return false;
        }

        TownMember member = new TownMember(this, player, rankId, civRankId);
        boolean ok = Database.getConnection().execute("INSERT INTO town_members (PlayerId,TownId,Rank,CivRank) VALUES(?,?,?,?)",
                member.getId(),
                member.getTown().getId(),
                rankId,
                civRankId
        );

        if (!ok) {
            return false;
        }

        player.setTown(this);

        player.sendSuccess("joined_the_town");
        this.broadcast(RANK_MEMBER, "s1_joined_the_town", player.getName());
        this.members.put(member.getId(), member);

        return ok;
    }

    /**
     * Удаляет участника.
     *
     * @param id идентификатор участника.
     * @param isKicked
     */
    public void removeMember(@NotNull UUID id, boolean isKicked) {
        TownMember exMember = this.members.remove(id);
        if (exMember == null) {
            return;
        }

        if (exMember.isOnline()) {
            KPlayer player = exMember.getPlayer();
            player.setTown(null);
        } else {
            PlayerCache.updatePlayerTown(id, null);
        }

        this.deleteMemberFromDB(id);
    }

    /**
     * Удаляет участника из базы данных.
     *
     * @param id идентификатор участника.
     */
    public void deleteMemberFromDB(@NotNull UUID id) {
        Database.getConnection().execute("DELETE FROM town_members WHERE PlayerId=?", id);
    }

    /**
     * Получает участника по идентификатору.
     *
     * @param id идентификатор участника.
     * @return участник или {@code null}, если не найден.
     */
    @Nullable
    public TownMember getMember(@NotNull UUID id) {
        return this.members.get(id);
    }

    /**
     * Получает участника по имени.
     *
     * @param name имя участника.
     * @return участник или {@code null}, если не найден.
     */
    @Nullable
    public TownMember getMember(@NotNull String name) {
        for (TownMember member : this.members.values()) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Получает список участников.
     *
     * @return список участников.
     */
    @NotNull
    public List<TownMember> getMembersList() {
        return this.members.values().stream().collect(Collectors.toList());
    }

    /**
     * Получает количество участников этого города.
     *
     * @return количество участников.
     */
    public int getMembersCount() {
        return this.members.size();
    }

    /**
     * Получает список участников, которые находятся в игре.
     *
     * @return список участников.
     */
    @NotNull
    public List<TownMember> getOnlineMembersList() {
        return this.members.values().stream().filter(m -> m.isOnline()).collect(Collectors.toList());
    }

    /**
     * Получает список участников, которые имеют заданный ранг.
     *
     * @param rankId ранг.
     * @return список участников.
     */
    @NotNull
    public List<TownMember> getMembersList(int rankId) {
        return this.members.values().stream().filter(m -> (m.getRankId() == rankId)).collect(Collectors.toList());
    }

    /**
     * Создает ранг.
     *
     * @param name имя ранга.
     * @param rights права ранга.
     * @return {@code true}, если ранг добавлен. Иначе {@code false}.
     */
    public boolean createRank(@NotNull String name, long rights) {
        int newRankId = this.getRanksCount();

        TownRank rank = new TownRank(this, newRankId, name, rights);
        rank.saveToDB();

        this.ranks.put(rank.getId(), rank);
        return true;
    }

    /**
     * Удаляет ранг.
     *
     * @param rankId идентификатор ранга.
     */
    public void removeRank(int rankId) {
        TownRank exRank = this.ranks.remove(rankId);
        if (exRank == null) {
            return;
        }

        Database.getConnection().execute("DELETE FROM town_ranks WHERE TownId=? AND RankId=?", this.getId(), rankId);
    }

    /**
     * Получает ранг по идентификатору.
     *
     * @param id идентификатор ранга.
     * @return ранг или {@code null}, если не найден.
     */
    @Nullable
    public TownRank getRank(int id) {
        return this.ranks.get(id);
    }

    /**
     * Получает список всех рангов.
     *
     * @return список рангов.
     */
    @NotNull
    public List<TownRank> getRanksList() {
        return this.ranks.values().stream().collect(Collectors.toList());
    }

    /**
     * Получает количество всех рангов этого города.
     *
     * @return количество рангов.
     */
    public int getRanksCount() {
        return this.ranks.size();
    }

    /**
     * Получает права ранга.
     *
     * @param rankId идентификатор ранга.
     * @return права ранга.
     */
    public long getRankRight(int rankId) {
        TownRank rank = this.ranks.get(rankId);
        if (rank != null) {
            return rank.getRights();
        }
        return RankRights.RIGHT_EMPTY;
    }

    /**
     * Определяет, имеет ли игрок право в этом городе.
     *
     * @param player игрок.
     * @param right право.
     * @return {@code true}, если игрок имеет право. Иначе {@code false}.
     */
    public boolean hasRankRights(@NotNull KPlayer player, long right) {
        TownMember member = this.getCiv().getMember(player.getObjectId());
        if (member != null) {
            if (((this.getCiv().getRankRight(member.getCivRankId()) & right) != RankRights.RIGHT_EMPTY)) {
                return true;
            }
            return ((this.getRankRight(member.getRankId()) & right) != RankRights.RIGHT_EMPTY);
        }
        return false;
    }

    /**
     * Получает количество золота в казне города.
     *
     * @return количество золота.
     */
    public long getGold() {
        return this.gold;
    }

    /**
     * Изменяет количество золота в казне города.
     *
     * @param amount величина, на которую необходимо изменить золото.
     */
    public void changeGold(long amount) {
        if (amount > 0) {
            // deposit
            if (this.gold + amount > Long.MAX_VALUE) {
                amount = Long.MAX_VALUE - this.gold;
            }

            if (amount > 0) {
                this.gold += amount;
                Database.getConnection().asyncExecute("UPDATE towns SET Gold=? WHERE TownId=?", this.getGold(), this.getId());
            }
        } else if (amount < 0) {
            // withdraw
            if (this.gold + amount < 0) {
                amount = -this.gold;
            }

            if (amount < 0) {
                this.gold += amount;
                Database.getConnection().asyncExecute("UPDATE towns SET Gold=? WHERE TownId=?", this.getGold(), this.getId());
            }
        }
    }

    /**
     * Получает время создания города.
     *
     * @return время.
     */
    public long getCreatedTime() {
        return this.createdTime;
    }

    /**
     * @return список всех структур.
     */
    @NotNull
    public Collection<Building> getBuildings() {
        return this.buildings.values();
    }

    /**
     * @return список всех строящихся структур.
     */
    @NotNull
    public Collection<Building> getStructuresInProgress() {
        return this.buildings.values().stream().filter(s -> !s.isInBuild).collect(Collectors.toList());
    }

    /**
     * Добавляет культурный чанк.
     *
     * @param cultureChunk - культурный чанк, который нужно добавить.
     */
    public void addCultureChunk(@NotNull CultureChunk cultureChunk) {
        this.cultureChunks.put(cultureChunk.getCoord(), cultureChunk);
    }

    /**
     * Удаляет культурный чанк.
     *
     * @param cultureChunk - культурный чанк, который нужно удалить.
     */
    public void removeCultureChunk(@NotNull CultureChunk cultureChunk) {
        this.cultureChunks.remove(cultureChunk.getCoord());
    }

    /**
     * Удаляет культурный чанк.
     *
     * @param coord - координаты культурного чанка в мире.
     */
    public void removeCultureChunk(@NotNull ChunkCoord coord) {
        this.cultureChunks.remove(coord);
    }

    /**
     * @param coord - координаты культурного чанка в мире.
     * @return культурный чанк, или {@code null} если не найден.
     */
    @Nullable
    public CultureChunk getCultureChunk(@NotNull ChunkCoord coord) {
        return this.cultureChunks.get(coord);
    }

    /**
     * @return список всех культурных чанков.
     */
    @NotNull
    public Collection<CultureChunk> getCultureChunks() {
        return this.cultureChunks.values();
    }

    /**
     * Обрезает старые культурный чанки.
     *
     * @param cultureChunks - список новых культурных чанков.
     */
    public void trimCultureChunks(@NotNull Set<ChunkCoord> cultureChunks) {
        List<ChunkCoord> oldCultureChunks = Lists.newArrayList(this.cultureChunks.keySet());
        oldCultureChunks.removeAll(cultureChunks);

        oldCultureChunks.stream().map(coord -> this.cultureChunks.remove(coord)).forEach(cultureChunk -> {
            CivWorld world = cultureChunk.getWorld();
            if (world != null) {
                world.removeCultureChunk(cultureChunk);
            }
        });
    }

    /**
     * Добавляет приграничный город.
     *
     * @param town - приграничный город, который нужно добавить.
     */
    public void addBorderTown(@NotNull Town town) {
        if (this.borderTowns.containsKey(town.getId())) {
            return;
        }
        this.borderTowns.put(town.getId(), town);
    }

    /**
     * Удаляет приграничный город.
     *
     * @param town - приграничный город, который нужно удалить.
     */
    public void removeBorderTown(@NotNull Town town) {
        if (!this.borderTowns.containsKey(town.getId())) {
            return;
        }
        this.borderTowns.remove(town.getId());
    }

    /**
     * Получает приграничный город, если имеется.
     *
     * @param townId - главный идентификатор города.
     * @return город, или {@code null} если не найден.
     */
    @Nullable
    public Town getBorderTown(@NotNull UUID townId) {
        return this.borderTowns.get(townId);
    }

    /**
     * @return список всех приграничных городов.
     */
    @NotNull
    public Collection<Town> getBorderTowns() {
        return this.borderTowns.values();
    }

    /**
     * Очищает список приграничный городов.
     */
    public void clearBorderTowns() {
        this.borderTowns.clear();
    }

    /**
     * Информирует город о новом объекте.
     *
     * Эта функция вызывается, когда указанный объект становится частью города.
     * Это происходит, когда объект создан или захвачен.
     *
     * @param object - объект, который теперь является частью города.
     */
    public void addTracking(@NotNull Building object) {
        this.buildings.put(object.getObjectId(), object);
    }

    /**
     * Информирует город об удалении объекта.
     *
     * Эта функция вызывается, когда указанный объект больше не является частью
     * города. Это происходит, когда объект уничтожен или захвачен.
     *
     * @param object - объект, который больше не является частью города.
     */
    public void removeTracking(@NotNull Building object) {
        this.buildings.remove(object.getObjectId());
    }

    @NotNull
    public boolean build(@NotNull StructureData type, @NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull Schematic schematic) {
        Structure structure = type.createBuilding(UUID.randomUUID(), world);
        structure.setTown(this);

        structure.setSchematicUndoPath("templates/undo/" + this.getId().toString() + "/" + structure.getObjectId().toString() + ".def");

        boolean ok = structure.create(world, pos, direction, schematic);

        if (!ok) {
            return false;
        }

        BuildingManager.addBuilding(structure);
        this.addTracking(structure);

        structure.isValidated();
        structure.startBuildTask();
        System.out.println("Start build");
        return true;
    }

    @NotNull
    public Result<Structure> build(@NotNull BuildContext<StructureData> context) {
        Result reuslt = new Result();
        BuildingPlacer placer = new BuildingPlacer(context.getData(), context.getSchematic());
        BuildError buildResult = placer.canPlaceHere(context.getWorld(), context.getPosition().getX(), context.getPosition().getY(), context.getPosition().getZ());
        if (buildResult != BuildError.OK) {
            return reuslt.setError(buildResult.ordinal());
        }

        Structure structure = context.getData().createBuilding(UUID.randomUUID(), context.getWorld());
        structure.setTown(this);

        structure.setSize(context.getSchematic().getSize());
        structure.setCoord(new BlockPos(context.getPosition().getX(), context.getPosition().getY(), context.getPosition().getZ()));
        structure.setDirection(context.getFacing());
        structure.setSchematicPath(context.getSchematic().getFilepath());
        structure.setSchematicUndoPath("templates/undo/" + this.getId().toString() + "/" + structure.getObjectId().toString() + ".def");

        this.addTracking(structure);
        context.getWorld().addStructure(structure);

        structure.isValidated();
        structure.startBuildTask();
        System.out.println("Start build");
        return reuslt.setResult(structure);
    }

    /**
     * Загружает город из базы данных.
     *
     * @param row
     * @return {@code true}, если город успешно загружен. Иначе {@code false}.
     */
    public boolean loadFromDB(@NotNull DatabaseRow row) {
        this.setName(row.getString("Name"));

        String leaderId = row.getString("LeaderId");
        if (leaderId != null && !leaderId.isEmpty()) {
            this.leaderId = UUID.fromString(leaderId);
        }

        this.civ = CivManager.getCiv(UUID.fromString(row.getString("CivId")));
        this.motherCiv = CivManager.getCiv(UUID.fromString(row.getString("MotherCivId")));

        this.gold = Math.max(0, row.getLong("Gold"));

        this.createdTime = row.getLong("CreatedDate");

        return true;
    }

    public void loadRankFromDB(@NotNull DatabaseRow row) {
        TownRank rank = new TownRank(this, row.getInt("Rank"), row.getString("Name"), row.getLong("Rights"));
        this.ranks.put(rank.getId(), rank);
    }

    public boolean loadMemberFromDB(@NotNull DatabaseRow row) {
        UUID playerId = UUID.fromString(row.getString("PlayerId"));

        if (this.members.containsKey(playerId)) {
            CivLogger.log(Level.SEVERE, "Tried to add {0} to town {1}. Member already exists.", playerId, this.getName());
            return false;
        }

        TownMember member = new TownMember(this, playerId);
        if (!member.loadFromDB(row)) {
            CivLogger.log(Level.SEVERE, "Error loading town member (GUID: {0}), deleting.", playerId);
            this.deleteMemberFromDB(playerId);
            return false;
        }

        this.members.put(member.getId(), member);
        return true;
    }

    /**
     * Проверяет данные города, загруженные из базы данных.
     *
     * @return {@code false}, если город должен быть удален. Иначе {@code true}.
     *
     */
    public boolean validate() {
        boolean brokenRanks = false;
        int ranks = this.getRanksCount();
        if (ranks < 3) {
            CivLogger.log(Level.SEVERE, "Town {0} has invalid number of ranks, creating new...", this.getId());
            brokenRanks = true;
        } else {
            for (int rankId = 0; rankId < ranks; rankId++) {
                TownRank rank = this.getRank(rankId);
                if (rank == null || rank.getId() != rankId) {
                    CivLogger.log(Level.SEVERE, "Town {0} has broken rank id {1}, creating default set of ranks...", this.getId(), rankId);
                    brokenRanks = true;
                }
            }
        }

        if (brokenRanks) {
            this.ranks.clear();
            this.createDefaultRanks();
        }

        return true;
    }

    public void handleChangeName(@NotNull KPlayer player, @NotNull String name) {
        if (!this.hasRankRights(player, RankRights.TR_RIGHT_SETNAME)) {
            player.sendError("not_enough_rights");
            return;
        }

        if (name.length() < 3) {
            player.sendError("town_name_is_too_short", name, 3);
            return;
        } else if (name.length() > 24) {
            player.sendError("town_name_is_too_long", name, 24);
            return;
        } else if (!(StringUtils.isAlpha(name) && StringUtils.isAsciiPrintable(name))) {
            player.sendError("town_name_is_invalid", name);
            return;
        } else if (TownManager.getTown(name) != null) {
            player.sendError("town_name_is_currently_in_use", name);
            return;
        }

        this.name = name;
        Database.getConnection().execute("UPDATE towns SET Name=? WHERE TownId=?", name, this.getId());
        this.broadcast("town_name_has_been_changed", name);
    }

    public void handleChangeLeader(@NotNull KPlayer player, @NotNull String newLeaderName) {
        if (this.isLeader(player)) {
            return;
        }

        if (!this.hasRankRights(player, RankRights.TR_RIGHT_SETLEADER)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember oldLeader = this.leaderId != null ? this.getMember(this.leaderId) : null;
        TownMember newLeader = this.getMember(newLeaderName);
        if (newLeader == null) {
            player.sendError("member_not_in_town");
            return;
        }

        this.setLeaderId(newLeader);
        this.broadcast("town_leader_has_been_changed", newLeader.getName());
        if (oldLeader != null) {
            oldLeader.changeRank(RANK_OFFCIER);
        }
    }

    public void handleClaimLeader(@NotNull KPlayer player) {
        if (this.isLeader(player)) {
            return;
        }

        TownMember oldLeader = this.leaderId != null ? this.getMember(this.leaderId) : null;
        if (oldLeader != null) {
            //if (!oldLeader.isInactiveForDays(Config.CIV_LEADER_INACTIVE_DAYS)) {
            //    player.sendError("town_claim_leader_still_active");
            //    return;
            //}
        }

        TownMember newLeader = this.getMember(player.getObjectId());
        if (newLeader != null) {
            this.setLeaderId(newLeader);
            this.broadcast("town_leader_has_been_changed", newLeader.getName());
            if (oldLeader != null) {
                oldLeader.changeRank(RANK_OFFCIER);
            }
        }
    }

    public void handleInviteMember(@NotNull KPlayer player, @NotNull String playername) {
        if (!this.hasRankRights(player, RankRights.TR_RIGHT_INVITE)) {
            player.sendError("not_enough_rights");
            return;
        }

        KPlayer target = PlayerManager.getPlayer(playername);
        if (target == null) {
            player.sendError("player_not_found_s1", playername);
            return;
        }

        if (target.getTown() != null) {
            player.sendError("player_has_already_join_another_town", target.getName());
            return;
        }

        player.sendSuccess("send_town_invitation_to_s1", target.getName());
        ChatRequest request = new ChatRequest(player, player, 30) {
            @Override
            protected boolean accept(@NotNull KPlayer receiver, @NotNull String[] args) {
                TownHandler.handleTownInviteAccept(player);
                return false;
            }

            @Override
            protected void cancel(@Nullable KPlayer receiver) {
                if (receiver != null) {
                    receiver.sendSuccess("request_town_invitation_cancel", playername);
                    receiver.setTownInvited(null);
                }
            }
        };

        ChatRequestManager.instance.sendRequest(request, I18n.tl("request_town_invitation_text", player.getName(), this.getName()),
                I18n.tl("request_town_invitation_tooltip_text", player.getName(), this.getName()),
                "/town accept");
    }

    public void handleAcceptMember(@NotNull KPlayer player) {
        this.addMember(player, RANK_MEMBER, Civilization.RANK_MEMBER);
    }

    public void handleLeaveMember(@NotNull KPlayer player) {
        if (this.isLeader(player)) {
            player.sendError("leader_cant_leave_the_town");
            return;
        }

        this.removeMember(player.getObjectId(), false);

        player.sendSuccess("left_the_town");
        this.broadcast("s1_left_the_town", player.getName());
    }

    public void handleKickMember(@NotNull KPlayer player, @NotNull String playername) {
        if (!this.hasRankRights(player, RankRights.TR_RIGHT_KICK)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember member = this.getMember(playername);
        if (member == null) {
            player.sendError("member_not_in_town");
            return;
        }

        if (Objects.equal(player.getObjectId(), member.getId())) {
            player.sendError("invalid_target_youself");
            return;
        }

        if (member.getRankId() == RANK_MASTER) {
            player.sendSuccess("leader_cant_leave_the_town", member.getName());
            return;
        }

        this.removeMember(player.getObjectId(), true);

        player.sendSuccess("kick_the_town");
        this.broadcast("s1_kick_the_town", member.getName());
    }

    public void handleUpdateMemberRank(@NotNull KPlayer player, @NotNull String playername, boolean demote) {
        if (!this.hasRankRights(player, demote ? RankRights.TR_RIGHT_DEMOTE : RankRights.TR_RIGHT_PROMOTE)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember member = this.getMember(playername);
        if (member == null) {
            player.sendError("member_not_in_civ");
            return;
        }

        if (Objects.equal(player.getObjectId(), member.getId())) {
            player.sendError("invalid_target_youself");
            return;
        }

        TownMember memberMe = this.getCiv().getMember(player.getObjectId());
        int rankId = memberMe != null ? memberMe.getRankId() : 0;
        int civRankId = memberMe != null ? memberMe.getCivRankId() : 0;
        TownRank newRank;
        if (demote) {
            // Игрок может разжаловать только низший ранг
            if (member.getCivRankId() <= civRankId && member.getRankId() <= rankId) {
                player.sendError("not_enough_rights");
                return;
            }

            newRank = this.getRank(member.getRankId() + 1);
            if (newRank == null) {
                player.sendError("not_enough_rights");
                return;
            }
        } else {
            // Игрок может повысить только низший ранг
            if (member.getCivRankId() <= civRankId && member.getRankId() <= rankId) {
                player.sendError("not_enough_rights");
                return;
            }

            newRank = this.getRank(member.getRankId() - 1);
            if (newRank == null) {
                player.sendError("not_enough_rights");
                return;
            }
            
            if(newRank.getId() == RANK_MASTER) {
                player.sendError("not_enough_rights");
                return;
            }
        }

        member.changeRank(newRank.getId());
        if (demote) {
            player.sendSuccess("town_member_demoted", member.getName());
        } else {
            player.sendSuccess("town_member_promoted", member.getName());
        }
    }

    public void handleMemberDepositGold(@NotNull KPlayer player, long money) {
        if (this.getGold() >= Long.MAX_VALUE) {
            return;
        }

        money = Math.min(player.getGold(), money);

        if (this.getGold() + money > Long.MAX_VALUE) {
            money = Long.MAX_VALUE - this.getGold();
        }

        if (money <= 0) {
            return;
        }

        player.changeGold(-money);
        this.changeGold(money);
        player.sendSuccess("deposit_x_money_to_town_treasury", money);
    }

    public void handleMemberWithdrawGold(@NotNull KPlayer player, long money) {
        if (!this.hasRankRights(player, RankRights.TR_RIGHT_WITHDRAW_GOLD)) {
            player.sendError("not_enough_rights");
            return;
        }

        if (player.getGold() >= Long.MAX_VALUE) {
            player.sendMessage("treasury_is_full");
            return;
        }

        money = Math.min(this.getGold(), money);

        if (player.getGold() + money > Long.MAX_VALUE) {
            money = Long.MAX_VALUE - player.getGold();
        }

        if (money <= 0) {
            return;
        }

        this.changeGold(-money);
        player.changeGold(money);
        player.sendSuccess("withdraw_x_money_from_town_treasury", money);
    }

    public void broadcast(int right, @NotNull String key, Object... objects) {
        for (TownMember member : this.getMembersList()) {
            try {
                KPlayer player = member.getPlayer();
                if (player != null) {
                    if (right == -1 || this.hasRankRights(player, right)) {
                        player.sendMessage(ChatColor.GOLD + I18n.tl("chat_prefix_town") + " " + ChatColor.WHITE + I18n.tl(key, objects));
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    public void broadcast(@NotNull String key, Object... objects) {
        this.broadcast(-1, key, objects);
    }

}
