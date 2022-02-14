package ru.civwars.building;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import ru.databaseapi.database.DatabaseResult;
import ru.databaseapi.database.DatabaseRow;
import ru.databaseapi.database.table.ColumnType;
import ru.databaseapi.database.table.TableBuilder;
import ru.databaseapi.database.table.TableColumn;
import ru.civwars.CivLogger;
import ru.civwars.CivWars;
import ru.civwars.building.types.BuildingData;
import ru.civwars.database.Database;
import ru.civwars.init.BuildingTypes;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BuildingManager {

    private static CivWars plugin;

    private static final ConcurrentHashMap<UUID, Building> buildings = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if (BuildingManager.plugin != null) {
            return;
        }
        BuildingManager.plugin = plugin;

        new TableBuilder("buildings",
                new TableColumn("BuildingId", ColumnType.UUID).primary(true),
                new TableColumn("TypeId", ColumnType.INT),
                new TableColumn("WorldId", ColumnType.UUID),
                new TableColumn("LocX", ColumnType.INT),
                new TableColumn("LocY", ColumnType.INT),
                new TableColumn("LocZ", ColumnType.INT),
                new TableColumn("Direction", ColumnType.INT),
                new TableColumn("SchematicPath", ColumnType.MEDIUMTEXT).defaultValue("''"),
                new TableColumn("SchematicUndoPath", ColumnType.MEDIUMTEXT).defaultValue("''"),
                new TableColumn("Width", ColumnType.INT),
                new TableColumn("Height", ColumnType.INT),
                new TableColumn("Length", ColumnType.INT),
                new TableColumn("State", ColumnType.INT).defaultValue(0),
                new TableColumn("BuiltBlocksCount", ColumnType.INT).defaultValue(0),
                new TableColumn("IsDead", ColumnType.INT).defaultValue(0),
                new TableColumn("Health", ColumnType.INT).defaultValue(0),
                new TableColumn("TownId", ColumnType.UUID).defaultValue("''")
        ).create(Database.getConnection());
    }

    private BuildingManager() {
    }

    /**
     * Добавляет структуру.
     *
     * @param building структура.
     */
    public static final void addBuilding(@NotNull Building building) {
        buildings.put(building.getObjectId(), building);
        building.getWorld().addStructure(building);
    }

    /**
     * Удаляет структуру.
     *
     * @param building структура.
     */
    public static final void removeBuilding(@NotNull Building building) {
        buildings.remove(building.getObjectId());
        building.getWorld().removeStructure(building);
    }

    /**
     * Получает структуру по идентификатору.
     *
     * @param buildingId идетификатор структуры.
     * @return структура или {@code null}, если не найдена.
     */
    @Nullable
    public static Building getBuilding(@NotNull UUID buildingId) {
        return buildings.get(buildingId);
    }

    /**
     * Получает список всех структур.
     *
     * @return список структур.
     */
    @NotNull
    public static Collection<Building> getBuilding() {
        return buildings.values();
    }

    /**
     * Загружает структуры из базы данных.
     */
    public static void load() {
        // ============================ Load all buildings ============================
        CivLogger.log(Level.INFO, "Loading buildings...");
        {
            DatabaseResult result = Database.getConnection().query("SELECT * FROM buildings");
            int count = 0;
            for (DatabaseRow row : result.getRows()) {
                UUID buildingId = UUID.fromString(row.getString("BuildingId"));
                int typeId = row.getInt("TypeId");
                UUID worldId = UUID.fromString(row.getString("WorldId"));

                BuildingData type = BuildingTypes.get(typeId);
                CivWorld world = worldId != null ? WorldManager.getWorld(worldId) : null;
                if (type == null || world == null) {
                    Database.getConnection().query("DELETE FROM buildings WHERE BuildingId=?", buildingId);
                    continue;
                }

                Building building = type.createBuilding(buildingId, world);

                if (!building.loadFromDB(row)) {
                    Database.getConnection().query("DELETE FROM buildings WHERE BuildingId=?", buildingId);
                    continue;
                }

                addBuilding(building);
                if (building.getTown() != null) {
                    building.getTown().addTracking(building);
                }
                count++;
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Buildings", count);
        }
    }

    /**
     * Проверяет данные структур, загруженных из базы данных.
     */
    public static void validate() {
        CivLogger.log(Level.INFO, "Validating data of loaded buildings...");
        {
            Iterator<Building> it = buildings.values().iterator();
            while (it.hasNext()) {
                Building vb = it.next();
                if (vb != null && !vb.validate()) {
                    it.remove();
                }
            }

            CivLogger.log(Level.INFO, ">> Validated data of loaded buildings");
        }
    }
}
