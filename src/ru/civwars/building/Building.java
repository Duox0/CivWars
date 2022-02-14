package ru.civwars.building;

import ru.civwars.building.types.BuildingData;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.building.block.BuildingChest;
import ru.civwars.building.block.BuildingControlBlock;
import ru.civwars.building.validation.StructureValidatorResult;
import ru.civwars.object.GameObject;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.ChunkPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.Size3i;
import ru.civwars.world.CivWorld;
import ru.databaseapi.database.DatabaseRow;
import ru.civwars.database.Database;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class Building extends GameObject {

    public final Random rand = new Random();

    /* Контрольные данные, которые придают этой структуре его характеристики. */
    protected final BuildingData type;

    /* Мир, в котором расположена структура. */
    protected final CivWorld world;

    /* Координатное местоположение структуры. */
    private BlockPos coord = BlockPos.ORIGIN;

    /* Направление взгляда структуры. */
    private EnumFacing direction = EnumFacing.EAST;

    /* Размер структуры. */
    private Size3i size = Size3i.SIZE_NONE;

    /* Общее количество блоков, из которых состоит структура. */
    private int totalBlocksCount = 0;

    /**
     * Путь до схематика.
     *
     * Указывается при строительстве структуры.
     *
     * Требуется для обновления блоков.
     */
    private String schematicPath = null;

    /**
     * Путь до схематика восстановления.
     *
     * Указывается при строительстве структуры.
     *
     * Требуется для восстановления блоков, при удалении структуры.
     */
    private String schematicUndoPath = null;

    /* Задача AI структуры. */
    private ScheduledFuture<?> aiTask = null;

    // Blocks
    private final Map<BlockPos, BuildingBlock> blocks = Maps.newConcurrentMap();
    private final Map<BlockPos, BuildingChest> chests = Maps.newConcurrentMap();
    private final Map<BlockPos, BuildingControlBlock> controlPoints = Maps.newConcurrentMap();

    // Validator
    private final Map<Integer, BuildingLayer> layers = Maps.newConcurrentMap();
    private boolean isValidated = false;
    private boolean isValidating = false;

    private boolean isValid = true;

    private Town town = null;

    private boolean isDead = false;
    private int health = 100;

    public boolean isInBuild = false;

    /**
     * Создает новую структуру с заданным идентификатором, типом и миром.
     *
     * Позже, объект должен быть инициализирован вызовом {@link #create()} или
     * {@link #loadFromDB(DatabaseRow)}.
     *
     * @param id идентификатор структуры.
     * @param type тип структуры
     * @param world мир, в котором размещается структура.
     */
    public Building(@NotNull UUID id, @NotNull BuildingData type, @NotNull CivWorld world) {
        super(id);
        this.setName(type.getName());
        this.type = type;
        this.world = world;
    }

    /**
     * Инициализирует эту структуру.
     *
     * @param world мир, в котором размещается структура.
     * @param pos координата блока, где начинается структура.
     * @param direction направление взгляда структуры.
     * @param schematic схематик структуры.
     * @return {@code true}, если город сохранен. Иначе {@code false}.
     */
    public boolean create(@NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull Schematic schematic) {
        this.setSize(schematic.getSize());
        this.setCoord(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        this.setDirection(direction);
        this.setSchematicPath(schematic.getFilepath());

        boolean ok = Database.getConnection().execute("INSERT INTO buildings (BuildingId,TypeId,"
                + "WorldId,LocX,LocY,LocZ,Direction,"
                + "SchematicPath,"
                + "SchematicUndoPath,"
                + "Width,Height,Length,"
                + "TownId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                this.getObjectId(), this.getType().getId(),
                world.getId(), pos.getX(), pos.getY(), pos.getZ(), direction.getIndex(),
                this.getSchematicPath() != null ? this.getSchematicPath() : "",
                this.getSchematicUndoPath() != null ? this.getSchematicUndoPath() : "",
                this.getSize().width, this.getSize().height, this.getSize().length,
                this.getTown() != null ? this.getTown().getId() : ""
        );

        if (ok) {

        }

        return ok;
    }

    /**
     * @return контрольные данные, которые придают этой структуре его
     * характеристики.
     */
    @NotNull
    public final BuildingData getType() {
        return this.type;
    }

    @NotNull
    @Override
    public final CivWorld getWorld() {
        return this.world;
    }

    /**
     * Устанавливает координатное местоположение структуры.
     *
     * @param coord новое координатное местоположение.
     */
    public void setCoord(@NotNull BlockPos coord) {
        this.coord = coord;
    }

    /**
     * @return координатное местоположение структуры.
     */
    @NotNull
    public BlockPos getCoord() {
        return this.coord;
    }

    /**
     * @return координата структуры по оси X.
     */
    public final int getX() {
        return this.getCoord().getX();
    }

    /**
     * @return координата структуры по оси Y.
     */
    public final int getY() {
        return this.getCoord().getY();
    }

    /**
     * @return координата структуры по оси Z.
     */
    public final int getZ() {
        return this.getCoord().getZ();
    }

    /**
     * Получает центральную координату структуры.
     *
     * @return
     */
    @NotNull
    public BlockPos getCenterCoord() {
        if (this.size == Size3i.SIZE_NONE) {
            return this.coord;
        }
        return this.coord.add((this.size.width / 2) + (this.size.width > 16 ? -1 : 0),
                (this.size.height / 2) + (this.size.height > 16 ? -1 : 0),
                (this.size.length / 2) + (this.size.length > 16 ? -1 : 0));
    }

    /**
     * Устанавливает направление взгляда структуры.
     *
     * @param direction новое направление взгляда.
     */
    public void setDirection(@NotNull EnumFacing direction) {
        this.direction = direction;
    }

    /**
     * @return направление взгляда структуры.
     */
    @NotNull
    public final EnumFacing getDirection() {
        return this.direction;
    }

    /**
     * Устанавливает размер структуры.
     *
     * @param newSize новый размер структуры.
     */
    public void setSize(@Nullable Size3i newSize) {
        if (!Objects.equal(this.size, newSize)) {
            if (newSize == null) {
                this.size = Size3i.SIZE_NONE;
                this.totalBlocksCount = 0;
            } else {
                this.size = new Size3i(newSize);
                this.totalBlocksCount = this.size.width * this.size.height * this.size.length;
            }
        }
    }

    /**
     * Устанавливает размер структуры.
     *
     * @param width новая ширина структуры.
     * @param height новая высота структуры.
     * @param length новая длина структуры.
     */
    public final void setSize(int width, int height, int length) {
        width = Math.max(0, width);
        height = Math.max(0, height);
        length = Math.max(0, length);
        this.setSize(new Size3i(width, height, length));
    }

    /**
     * @return размер постройки, или {@code null}, если размер не задан.
     */
    @Nullable
    public final Size3i getSize() {
        return this.size;
    }

    /**
     * @return общее количество блоков, из которых состоит структура.
     */
    public int getTotalBlocksCount() {
        return this.totalBlocksCount;
    }

    /**
     * Устанавливает путь до схематика.
     *
     * @param schematicPath путь до схематика.
     */
    public final void setSchematicPath(@NotNull String schematicPath) {
        this.schematicPath = schematicPath.trim().isEmpty() ? "" : schematicPath;
    }

    /**
     * Получает путь до схематика структуры.
     *
     * @return путь до схематика или {@code null}, если не задан.
     */
    @Nullable
    public final String getSchematicPath() {
        return this.schematicPath;
    }

    /**
     * Устанавливает путь до схематика восстановления.
     *
     * @param schematicUndoPath путь до схематика восстановления.
     */
    public final void setSchematicUndoPath(@NotNull String schematicUndoPath) {
        this.schematicUndoPath = schematicUndoPath.trim().isEmpty() ? "" : schematicUndoPath;
    }

    /**
     * Получает путь до схематика восстановления структуры.
     *
     * @return путь до схематика восстановленияа или {@code null}, если не
     * задан.
     */
    @Nullable
    public final String getSchematicUndoPath() {
        return this.schematicUndoPath;
    }

    public void tick() {

    }

    /**
     * Информирует структуру о включении.
     */
    public void onEnable() {

    }

    /**
     * Информарует структуру о выключении.
     */
    public void onDisable() {

    }

    /**
     * Имя иконки для отображения на веб-карте; Подробности см. здесь
     * {@link BuildingData#getMarkerIcon()}.
     *
     * @return имя иконки.
     */
    @NotNull
    public String getMarkerIcon() {
        return this.getType().getMarkerIcon(this);
    }

    /**
     * @return отображаемое имя.
     */
    @NotNull
    public String getDisplayName() {
        return this.getType().getDisplayName(this);
    }

    /**
     * Получает описание структуры для отображения на веб-карте; Подробности см.
     * здесь {@link BuildingData#getDynmapDescription()}.
     *
     * @return описание структуры.
     */
    @Nullable
    public String getMarkerDescription() {
        return this.getType().getMarkerDescription(this);
    }

    @Override
    public final Location getLocation() {
        return new Location(this.world.getWorld(), this.getX(), this.getY(), this.getZ());
    }

    /**
     * Добавляет структурный блок.
     *
     * @param block - структурный блок, который нужно добавить.
     */
    public void addBlock(@NotNull BuildingBlock block) {
        if (this.blocks.containsKey(block.getPosition())) {
            return;
        }
        this.blocks.put(block.getPosition(), block);

        if (block instanceof BuildingChest) {
            this.chests.put(block.getPosition(), (BuildingChest) block);
        } else if (block instanceof BuildingControlBlock) {
            this.controlPoints.put(block.getPosition(), (BuildingControlBlock) block);
        }
    }

    /**
     * Удаляет структурный блок.
     *
     * @param block - структурный блок, который нужно удалить.
     */
    public void removeBlock(@NotNull BuildingBlock block) {
        if (!this.blocks.containsKey(block.getPosition())) {
            return;
        }
        this.blocks.remove(block.getPosition());

        if (block instanceof BuildingChest) {
            this.chests.remove(block.getPosition());
        } else if (block instanceof BuildingControlBlock) {
            this.controlPoints.remove(block.getPosition());
        }
    }

    /**
     * @return список всех структурных блоков.
     */
    @NotNull
    public Collection<BuildingBlock> getBlocks() {
        return this.blocks.values();
    }

    /**
     * @param pos - позиция в мире.
     * @return структурный сундук или null, если структурный сундук на данных
     * координатах не найден.
     */
    @Nullable
    public BuildingChest getChest(@NotNull BlockPos pos) {
        return this.chests.get(pos);
    }

    /**
     * @return список всех структурных сундуков.
     */
    @NotNull
    public Collection<BuildingChest> getChests() {
        return this.chests.values();
    }

    /**
     * @param chestId - идентификатор сундука.
     * @return список сундуков с идентификатором chestId.
     */
    @NotNull
    public Collection<BuildingChest> getChests(int chestId) {
        return this.chests.values().stream().filter(m -> (m.getChestId() == chestId)).collect(Collectors.toList());
    }

    /**
     * @param pos - позиция в мире.
     * @return контрольный блок, или {@code null}, если кнотрольный блок на
     * данных координатах не найден.
     */
    @Nullable
    public BuildingControlBlock getControlBlock(@NotNull BlockPos pos) {
        return this.controlPoints.get(pos);
    }

    /**
     * @return список всех контрольных блоков.
     */
    @NotNull
    public Collection<BuildingControlBlock> getgetControlBlocks() {
        return this.controlPoints.values();
    }

    @NotNull
    public List<ChunkPos> getBusyChunks() {
        int firstChunkX = this.getX() >> 4;
        int firstChunkZ = this.getZ() >> 4;
        int lastChunkX = firstChunkX + ((this.getSize().width - 1) >> 4) + 1;
        int lastChunkZ = firstChunkZ + ((this.getSize().length - 1) >> 4) + 1;

        List<ChunkPos> chunks = Lists.newArrayList();

        for (int zz = firstChunkZ; zz < lastChunkZ; zz++) {
            for (int xx = firstChunkX; xx < lastChunkX; xx++) {
                chunks.add(new ChunkPos(xx, zz));
            }
        }
        return chunks;
    }

    /**
     * Добавляет информацию о слое фундамента.
     *
     * @param y - высота слоя.
     * @param layer - слой.
     */
    public void addLayer(int y, @NotNull BuildingLayer layer) {
        this.layers.put(y, layer);
    }

    /**
     *
     * @param y - высота слоя.
     * @return информация о слое фундамента.
     */
    @Nullable
    public final BuildingLayer getLayer(int y) {
        return this.layers.get(y);
    }

    /**
     * Является ли фундамент проверенным. В случае необходимости запускает
     * проверку.
     *
     * @return {@code true}, если фундамент проверен. Иначе {@code false}.
     */
    public boolean isValidated() {
        if (!this.isValidated) {
            if (this.isValidating) {
                return false;
            }
            this.isValidating = true;
            return false;
        }
        return this.isValidated;
    }

    /**
     * Устанавливает результат проверки фундамента.
     *
     * @param result результат проверки.
     */
    public void setIsValidated(@NotNull StructureValidatorResult result) {
        this.isValidated = true;
        if (this.isValidating) {
            this.isValidating = false;
        }

        this.layers.clear();
        for (int y : result.getLayersLevel()) {
            BuildingLayer layer = result.getLayer(y);
            this.addLayer(y, layer);
        }
        this.setValid(result.isValid());
    }

    /**
     *
     * @param valid - состояние фундамента.
     */
    public void setValid(boolean valid) {
        this.isValid = valid;
    }

    /**
     * Получает значение, определяющее состояние фундамента.
     *
     * @return true, если фундамент в необходимом состоянии, иначе false.
     */
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * Устанавливает город.
     *
     * @param town идентификатор города.
     */
    public void setTown(@Nullable Town town) {
        this.town = town;
    }

    /**
     * @return идентификатор города или {@code null}, если город не задан.
     */
    @Nullable
    public final Town getTown() {
        return this.town;
    }

    /**
     * Устанавливает мертвое состояние.
     *
     * @param newIsDead - новое мертвое состояние.
     */
    public void setIsDead(boolean newIsDead) {
        this.isDead = newIsDead;
    }

    /**
     * Находится ли в мертвом состоянии.
     *
     * @return {@code true}, если находится в мертвом состоянии. Иначе
     * {@code false}.
     */
    public boolean isDead() {
        return this.isDead;
    }

    /**
     * Устанавливает прочность структуры. {@code isDead} будет установлено
     * на{@code health} <= 0.
     *
     * @param health - новая прочность структуры.
     */
    public void setHealth(int health) {
        if (health < 0) {
            health = 0;
        }

        this.health = health;
        if (this.health <= 0 && !this.isDead) {
            this.isDead = true;
        } else if (this.isDead) {
            this.isDead = false;
        }
    }

    /**
     * @return текущая прочность структуры.
     */
    public int getHealth() {
        return this.health;
    }

    public boolean isDamagedBlock(@NotNull BuildingBlock block) {
        return (this.controlPoints.isEmpty() || (block instanceof BuildingControlBlock));
    }

    public final boolean damage(int amount) {
        if (this.isDead) {
            return false;
        }

        this.setHealth(this.health - amount);

        return true;
    }

    public boolean damage(@NotNull KPlayer player, int amount) {
        if (this.isDead) {
            return false;
        }

        if (!this.damage(amount)) {
            return false;
        }

        return true;
    }

    public void onControlBlockDestroy(@NotNull BuildingControlBlock control, @NotNull CivWorld world, @NotNull KPlayer player) {
        boolean allDestroyed = true;
        for (BuildingControlBlock cb : this.controlPoints.values()) {
            if (!cb.isDestroyed()) {
                allDestroyed = false;
                break;
            }
        }

        System.out.println("Destroy building");
    }

    /**
     * Загружает структуру из базы данных.
     *
     * @param row
     * @return {@code true}, если структура загружена. Иначе {@code false}.
     */
    public boolean loadFromDB(@NotNull DatabaseRow row) {
        this.setSize(row.getInt("Width"), row.getInt("Height"), row.getInt("Length"));
        this.setCoord(new BlockPos(row.getInt("LocX"), row.getInt("LocY"), row.getInt("LocZ")));
        this.setDirection(EnumFacing.getHorizontal(row.getInt("Direction")));

        this.setSchematicPath(row.getString("SchematicPath"));
        this.setSchematicUndoPath(row.getString("SchematicUndoPath"));

        /*Schematic schematic = SchematicManager.getSchematic(this.getSchematicPath());
        if (schematic != null) {
            World world = this.getWorld().getWorld();
            for (int i = 0; i < this.getBuiltBlocksCount(); i++) {
                int y = (i / (this.getSize().width * this.getSize().length));
                int z = (i / this.getSize().width) % this.getSize().length;
                int x = i % this.getSize().width;

                SchematicBlock sb = schematic.getBlock(x, y, z);
                if (!sb.isCommand()) {
                    if (sb.getBlockId() != 0) {
                        BlockPos pos = new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
                        BuildingBlock bb = new BuildingBlock(pos, this);
                        this.addBlock(bb);
                        this.getWorld().addBuildingBlock(bb);
                    }
                }
            }

            if (this.isCompleted()) {
                for (BlockPos pos : schematic.getCommandBlocks()) {
                    SchematicBlockCommand sb = (SchematicBlockCommand) schematic.getBlock(pos.getX(), pos.getY(), pos.getZ());
                    Block block = world.getBlockAt(this.getX() + pos.getX(), this.getY() + pos.getY(), this.getZ() + pos.getZ());
                    sb.onPostBuild(this, block);
                }
            }
        }*/

        return true;
    }

    /**
     * Проверяет данные структуры, загруженные из базы данных.
     *
     * @return {@code false}, если структура должна быть удалена. Иначе
     * {@code true}.
     *
     */
    public boolean validate() {
        return true;
    }
}
