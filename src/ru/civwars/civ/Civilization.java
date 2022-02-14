package ru.civwars.civ;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import ru.civwars.CivLogger;
import ru.civwars.database.Database;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.town.Town;
import ru.civwars.town.TownMember;
import ru.databaseapi.database.DatabaseRow;
import ru.civwars.i18n.I18n;
import ru.civwars.town.RankRights;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class Civilization {

    public static final int RANK_MASTER = 0;
    public static final int RANK_OFFCIER = 1;
    public static final int RANK_MEMBER = 2;

    public final Random random = new Random();

    /* Идентификатор цивилизации. */
    private final UUID id;

    /* Имя цивилизации. */
    private String name;

    /* Идентификатор лидера. */
    private UUID leaderId = null;

    /* Идентификатор главного города. */
    private UUID capitalId = null;

    /* Карта рангов, проиндексированных по идентификатору ранга. */
    private final ConcurrentHashMap<Integer, CivRank> ranks = new ConcurrentHashMap<>();

    /* Карта городов, проиндексированных по идентификатору городу. */
    private final ConcurrentHashMap<UUID, Town> towns = new ConcurrentHashMap<>();

    /* Золото цивилизации. */
    private long gold = 0;

    /* Время создания цивилизации. */
    private long createdTime = 0;

    /**
     * Создает новую цивилизацию с заданным идентификатором.
     *
     * Позже, объект должен быть инициализирован вызовом
     * {@link #create(KPlayer, String)} или {@link #loadFromDB(DatabaseRow)}.
     *
     * @param id идентификатор цивилизации.
     */
    public Civilization(@NotNull UUID id) {
        this.id = id;
    }

    /**
     * Инициализирует эту цивилизацию и сохраняет в базу данных.
     *
     * @param leader лидер цивилизации.
     * @param name имя цивилизации.
     * @return {@code true}, если цивилизация сохранена. Иначе {@code false}.
     */
    public boolean create(@NotNull KPlayer leader, @NotNull String name) {
        if (CivManager.getCiv(name) != null || leader.getTown() != null) {
            return false;
        }

        CivLogger.log(Level.INFO, "Creating civilization [{0}] for leader {1} ({2})", new Object[]{name, leader.getName(), leader.getObjectId().toString()});

        long now = System.currentTimeMillis();

        this.name = name;
        this.leaderId = leader.getObjectId();
        this.createdTime = now;

        boolean ok = Database.getConnection().execute("INSERT INTO civilizations (CivId,Name,LeaderId,CreatedDate) VALUES(?,?,?,?)",
                this.getId(),
                this.getName(),
                this.getLeaderId(),
                this.getCreatedTime()
        );

        if (ok) {
        }

        return ok;
    }
    
    private void createDefaultRanks() {
        Database.getConnection().execute("DELETE FROM civilization_ranks WHERE CivId=?", this.getId());
        this.createRank("Master", RankRights.RIGHTS_CIV_MASTER);
        this.createRank("Offcier", RankRights.RIGHTS_CIV_OFFICER);
    }

    /**
     * Распускает цивилизацию и удаляет все связанные данные из базы данных.
     */
    public void disband() {
        Collection<Town> towns = this.towns.values();
        for (Town town : towns) {
            this.removeTown(town);
        }

        Database.getConnection().execute("DELETE FROM civilizations WHERE CivId=?", this.getId());

        CivManager.removeCiv(this);
    }

    /**
     * Получает идентификатор цивилизации.
     *
     * @return идентификатор цивилизации.
     */
    @NotNull
    public final UUID getId() {
        return this.id;
    }

    /**
     * Устанавливает имя цивилизации.
     *
     * @param name имя.
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * Получает имя цивилизации.
     *
     * @return имя цивилизации.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * Устанавливает участника новым лидером цивилизации.
     *
     * @param member новый лидер.
     */
    public void setLeaderId(@NotNull TownMember member) {
        this.leaderId = member.getId();
        Database.getConnection().asyncExecute("UPDATE civilizations SET LeaderId=? WHERE CivId=?", this.leaderId, this.getId());
    }

    /**
     * Получает идентификатор лидера цивилизации.
     *
     * @return идентификатор лидера или {@code null}, если не задан.
     */
    @Nullable
    public UUID getLeaderId() {
        return this.leaderId;
    }

    /**
     * Определяет, является ли игрок лидером цивилизации.
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
     * Устанавливает город новой столицей цивилизации.
     *
     * @param town новый столица.
     */
    public void setCapitalId(@NotNull Town town) {
        this.capitalId = town.getId();
        Database.getConnection().asyncExecute("UPDATE civilizations SET CapitalId=? WHERE CivId=?", this.capitalId, this.getId());
    }

    /**
     * Получает идентификатор главного города.
     *
     * @return идентификатор города или {@code null}, если не задан.
     */
    @Nullable
    public UUID getCapitalId() {
        return this.capitalId;
    }

    /**
     * Определяет, является ли город столицей цивилизации.
     *
     * @param town игрок.
     * @return {@code true}, если город является столицей цивилизации. Иначе
     * {@code false}.
     */
    public boolean isCapital(@NotNull Town town) {
        if (Objects.equal(this.capitalId, town.getId())) {
            return true;
        }
        return false;
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

        CivRank rank = new CivRank(this, newRankId, name, rights);
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
        CivRank exRank = this.ranks.remove(rankId);
        if (exRank == null) {
            return;
        }

        Database.getConnection().execute("DELETE FROM civilization_ranks WHERE CivId=? AND RankId=?", this.getId(), rankId);
    }

    /**
     * Получает ранг по идентификатору.
     *
     * @param id идентификатор ранга.
     * @return ранг или {@code null}, если не найден.
     */
    @Nullable
    public CivRank getRank(int id) {
        return this.ranks.get(id);
    }

    /**
     * Получает список всех рангов.
     *
     * @return список рангов.
     */
    @NotNull
    public List<CivRank> getRanksList() {
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
        CivRank rank = this.ranks.get(rankId);
        if (rank != null) {
            return rank.getRights();
        }
        return RankRights.RIGHT_EMPTY;
    }

    public boolean hasRankRights(@NotNull KPlayer player, long right) {
        TownMember member = this.getMember(player.getObjectId());
        if (member != null) {
            return ((this.getRankRight(member.getCivRankId()) & right) != RankRights.RIGHT_EMPTY);
        }
        return false;
    }

    /**
     * Добавляет город.
     *
     * @param town город.
     */
    public void addTown(@NotNull Town town) {
        this.towns.put(town.getId(), town);
    }

    /**
     * Удаляет город.
     *
     * @param town город.
     */
    public void removeTown(@NotNull Town town) {
        this.towns.remove(town.getId());
    }

    /**
     * Получает город по имени.
     *
     * @param name имя города.
     * @return город или {@code null}, если не найден.
     */
    @Nullable
    public Town getTown(@NotNull String name) {
        for (Town town : this.towns.values()) {
            if (town.getName().equalsIgnoreCase(name)) {
                return town;
            }
        }
        return null;
    }

    /**
     * Получает список городов.
     *
     * @return список городов.
     */
    @NotNull
    public Collection<Town> getTownsList() {
        return this.towns.values().stream().collect(Collectors.toList());
    }

    /**
     * Получает количество городов.
     *
     * @return количество городов.
     */
    @NotNull
    public int getTownCount() {
        return this.towns.size();
    }

    /**
     * Получает участника по идентификатору.
     *
     * @param id идентификатор участника.
     * @return участник или {@code null}, если не найден.
     */
    @Nullable
    public TownMember getMember(@NotNull UUID id) {
        for (Town town : this.towns.values()) {
            TownMember member = town.getMember(id);
            if (member != null) {
                return member;
            }
        }
        return null;
    }

    /**
     * Получает участника по имени.
     *
     * @param name имя участника.
     * @return участник или {@code null}, если не найден.
     */
    @Nullable
    public TownMember getMember(@NotNull String name) {
        for (Town town : this.towns.values()) {
            TownMember member = town.getMember(name);
            if (member != null) {
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
        List<TownMember> members = Lists.newArrayList();
        for (Town town : this.towns.values()) {
            members.addAll(town.getMembersList());
        }
        return members;
    }

    /**
     * Получает список участников, которые находятся в игре.
     *
     * @return список участников.
     */
    @NotNull
    public List<TownMember> getOnlineMembersList() {
        List<TownMember> members = Lists.newArrayList();
        for (Town town : this.towns.values()) {
            members.addAll(town.getOnlineMembersList());
        }
        return members;
    }

    /**
     * Получает список участников, которые имеют заданный ранг.
     *
     * @param rankId ранг.
     * @return список участников.
     */
    @NotNull
    public List<TownMember> getMembersList(int rankId) {
        List<TownMember> members = Lists.newArrayList();
        for (Town town : this.towns.values()) {
            members.addAll(town.getMembersList(rankId));
        }
        return members;
    }

    /**
     * Получает количество золота в казне цивилизации.
     *
     * @return количество золота.
     */
    public long getGold() {
        return this.gold;
    }

    /**
     * Изменяет количество золота в казне цивилизации.
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
                Database.getConnection().asyncExecute("UPDATE civilizations SET Gold=? WHERE CivId=?", this.getGold(), this.getId());
            }
        } else if (amount < 0) {
            // withdraw
            if (this.gold + amount < 0) {
                amount = -this.gold;
            }

            if (amount < 0) {
                this.gold += amount;
                Database.getConnection().asyncExecute("UPDATE civilizations SET Gold=? WHERE CivId=?", this.getGold(), this.getId());
            }
        }
    }

    /**
     * Получает время создания цивилизации.
     *
     * @return время.
     */
    public long getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Загружает цивилизации из базы данных.
     *
     * @param row
     * @return {@code true}, если цивилизация успешно загружена. Иначе
     * {@code false}.
     */
    public boolean loadFromDB(@NotNull DatabaseRow row) {
        this.setName(row.getString("Name"));

        String leaderId = row.getString("LeaderId");
        if (leaderId != null && !leaderId.isEmpty()) {
            this.leaderId = UUID.fromString(leaderId);
        }

        String capitalId = row.getString("CapitalId");
        if (capitalId != null && !capitalId.isEmpty()) {
            this.capitalId = UUID.fromString(capitalId);
        }

        this.gold = Math.max(0, row.getLong("Gold"));

        this.createdTime = row.getLong("CreatedDate");

        return true;
    }

    public void loadRankFromDB(@NotNull DatabaseRow row) {
        CivRank rank = new CivRank(this, row.getInt("Rank"), row.getString("Name"), row.getInt("Rights"));
        this.ranks.put(rank.getId(), rank);
    }

    /**
     * Проверяет данные цивилизации, загруженные из базы данных.
     *
     * @return {@code false}, если цивилизация должна быть удалена. Иначе
     * {@code true}.
     *
     */
    public boolean validate() {
        boolean brokenRanks = false;
        int ranks = this.getRanksCount();
        if (ranks < 3) {
            CivLogger.log(Level.SEVERE, "Civilization {0} has invalid number of ranks, creating new...", this.getId());
            brokenRanks = true;
        } else {
            for (int rankId = 0; rankId < ranks; rankId++) {
                CivRank civRank = this.getRank(rankId);
                if (civRank == null || civRank.getId() != rankId) {
                    CivLogger.log(Level.SEVERE, "Civilization {0} has broken rank id {1}, creating default set of ranks...", this.getId(), rankId);
                    brokenRanks = true;
                }
            }
        }

        if (brokenRanks) {
            this.ranks.clear();
            this.createDefaultRanks();
        }

        if (this.capitalId == null) {
            // ищем самый мощный город
            Town newCapital = null;
            for (Town town : this.towns.values()) {
                if (town.getMotherCiv() == this) {
                    newCapital = town;
                }
            }

            if (newCapital != null) {
                this.setCapitalId(newCapital);
            }
        }

        return true;
    }

    public void handleChangeName(@NotNull KPlayer player, @NotNull String name) {
        if (this.getName().equalsIgnoreCase(name)) {
            return;
        }

        if (!this.isLeader(player)) {
            player.sendError("not_enough_rights");
            return;
        }

        if (name.length() < 3) {
            player.sendError("civ_name_is_too_short", name, 3);
            return;
        } else if (name.length() > 24) {
            player.sendError("civ_name_is_too_long", name, 24);
            return;
        } else if (!(StringUtils.isAlpha(name) && StringUtils.isAsciiPrintable(name))) {
            player.sendError("civ_name_is_invalid", name);
            return;
        } else if (CivManager.getCiv(name) != null) {
            player.sendError("civ_name_is_currently_in_use", name);
            return;
        }

        this.name = name;
        Database.getConnection().execute("UPDATE civilizations SET Name=? WHERE CivId=?", name, this.getId());

        this.broadcast(1, "civ_name_has_been_changed", name);
    }

    public void handleChangeLeader(@NotNull KPlayer player, @NotNull String newLeaderName) {
        if (!this.isLeader(player)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember oldLeader = this.getMember(player.getObjectId());
        if (oldLeader != null) {
            TownMember newLeader = this.getMember(newLeaderName);
            if (newLeader != null) {
                this.setLeaderId(newLeader);
                oldLeader.changeCivRank(1);
                this.broadcast(1, "civ_leader_has_been_changed", newLeader.getName());
            } else {
                player.sendError("member_not_in_civ");
            }
        }
    }

    public void handleClaimLeader(@NotNull KPlayer player) {
        if (this.isLeader(player)) {
            return;
        }

        TownMember oldLeader = this.getMember(player.getObjectId());
        if (oldLeader != null) {
            //if (!oldLeader.isInactiveForDays(Config.CIV_LEADER_INACTIVE_DAYS)) {
            //    player.sendError("civ_claim_leader_still_active");
            //    return;
            //}
        }

        TownMember newLeader = this.getMember(player.getObjectId());
        if (newLeader != null) {
            this.setLeaderId(newLeader);
            this.broadcast(1, "civ_leader_has_been_changed", newLeader.getName());
            if (oldLeader != null) {
                oldLeader.changeCivRank(1);
            }
        }
    }

    public void handleMod(@NotNull KPlayer player, @NotNull String newModName) {
        if (!this.isLeader(player)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember newMod = this.getMember(newModName);
        if (newMod != null) {
            if (newMod.getCivRankId() == 1) {
                newMod.changeCivRank(1);
                player.sendSuccess("civ_member_promoted", newMod.getName());
            }
        } else {
            player.sendError("member_not_in_civ");
        }
    }

    public void handleUnmod(@NotNull KPlayer player, @NotNull String modName) {
        if (!this.isLeader(player)) {
            player.sendError("not_enough_rights");
            return;
        }

        TownMember oldMod = this.getMember(modName);
        if (oldMod != null) {
            if (oldMod.getCivRankId() == 1) {
                oldMod.changeCivRank(1);
                player.sendSuccess("civ_member_demoted", oldMod.getName());
            }
        } else {
            player.sendError("member_not_in_civ");
        }
    }

    public void handleMemberDepositGold(@NotNull KPlayer player, long money) {
        if (this.getGold() >= Long.MAX_VALUE) {
            player.sendMessage("civ_treasury_is_full");
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
        player.sendSuccess("deposit_x_money_to_civ_treasury", money);
    }

    public void handleMemberWithdrawGold(@NotNull KPlayer player, long money) {
        if (!this.isLeader(player)) {
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
        player.sendSuccess("withdraw_x_money_from_civ_treasury", money);
    }

    public void broadcast(int right, @NotNull String key, Object... objects) {
        for (TownMember member : this.getMembersList()) {
            try {
                if (member.isOnline()) {
                    KPlayer player = member.getPlayer();
                    if (this.hasRankRights(player, right)) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + I18n.tl("chat_prefix_civ") + " " + ChatColor.WHITE + I18n.tl(key, objects));
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

}
