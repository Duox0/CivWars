package ru.civwars.town;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.database.Database;
import ru.databaseapi.database.DatabaseResult;
import ru.databaseapi.database.DatabaseRow;
import ru.databaseapi.database.table.ColumnType;
import ru.databaseapi.database.table.TableBuilder;
import ru.databaseapi.database.table.TableColumn;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class TownManager {

    private static CivWars plugin;

    private static final ConcurrentHashMap<UUID, Town> towns = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if (TownManager.plugin != null) {
            return;
        }
        TownManager.plugin = plugin;

        new TableBuilder("towns",
                new TableColumn("TownId", ColumnType.UUID).primary(true),
                new TableColumn("Name", ColumnType.VARCHAR_64),
                new TableColumn("CivId", ColumnType.UUID),
                new TableColumn("MotherCivId", ColumnType.UUID),
                new TableColumn("LeaderId", ColumnType.UUID).defaultValue("''"),
                new TableColumn("Gold", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("CreatedDate", ColumnType.BIGINT).defaultValue(0)
        ).create(Database.getConnection());

        new TableBuilder("town_members",
                new TableColumn("PlayerId", ColumnType.UUID).primary(true),
                new TableColumn("TownId", ColumnType.UUID),
                new TableColumn("Rank", ColumnType.INT).defaultValue(0),
                new TableColumn("CivRank", ColumnType.INT).defaultValue(0)
        ).create(Database.getConnection());

        new TableBuilder("town_ranks",
                new TableColumn("TownId", ColumnType.UUID),
                new TableColumn("RankId", ColumnType.INT),
                new TableColumn("Name", ColumnType.VARCHAR_32),
                new TableColumn("Rights", ColumnType.BIGINT)
        ).create(Database.getConnection());
    }

    private TownManager() {
    }

    /**
     * Добавляет город.
     *
     * @param town город.
     */
    public static final void addTown(@NotNull Town town) {
        towns.put(town.getId(), town);
    }

    /**
     * Удаляет город.
     *
     * @param town город.
     */
    public static final void removeTown(@NotNull Town town) {
        towns.remove(town.getId());
    }

    /**
     * Получает город по идентификатору.
     *
     * @param townId идетификатор города.
     * @return город или {@code null}, если не найден.
     */
    @Nullable
    public static Town getTown(@NotNull UUID townId) {
        return towns.get(townId);
    }

    /**
     * Получает город по имени.
     *
     * @param name имя города.
     * @return город или {@code null}, если не найден.
     */
    @Nullable
    public static Town getTown(@NotNull String name) {
        for (Town t : towns.values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Получает список всех городов.
     *
     * @return список городов.
     */
    @NotNull
    public static Collection<Town> getTowns() {
        return towns.values();
    }

    /**
     * Загружает города из базы данных.
     */
    public static void load() {
        // ============================ Load all towns ============================
        CivLogger.log(Level.INFO, "Loading towns...");
        {
            Database.getConnection().execute("DELETE t FROM towns t LEFT JOIN civilizations c ON t.CivId = c.CivId WHERE c.CivId IS NULL");

            DatabaseResult result = Database.getConnection().query("SELECT * FROM towns");
            int count = 0;
            for (DatabaseRow row : result.getRows()) {
                UUID townId = UUID.fromString(row.getString("TownId"));
                Town town = new Town(townId);
                if (!town.loadFromDB(row)) {
                    Database.getConnection().query("DELETE FROM towns WHERE TownId=?", townId);
                    continue;
                }

                addTown(town);
                if (town.getCiv() != null) {
                    town.getCiv().addTown(town);
                }
                count++;
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Towns", count);
        }

        // ============================ Load all town ranks ============================
        CivLogger.log(Level.INFO, "Loading town ranks...");
        {
            Database.getConnection().execute("DELETE tr FROM town_ranks tr LEFT JOIN towns t ON tr.TownId = t.TownId WHERE t.TownId IS NULL");

            DatabaseResult result = Database.getConnection().query("SELECT * FROM town_ranks");
            int count = 0;

            for (DatabaseRow row : result.getRows()) {
                UUID townId = UUID.fromString(row.getString("TownId"));
                Town town = getTown(townId);
                if (town != null) {
                    town.loadRankFromDB(row);
                    count++;
                }
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Town Ranks", count);
        }

        // ============================ Load all town members ============================
        CivLogger.log(Level.INFO, "Loading town members...");
        {
            Database.getConnection().execute("DELETE tm FROM town_members tm LEFT JOIN towns t ON tm.TownId = t.TownId WHERE t.TownId IS NULL");

            DatabaseResult result = Database.getConnection().query("SELECT tm.PlayerId,tm.TownId,Rank,CivRank, p.Name "
                    + "FROM town_members tm "
                    + "LEFT JOIN players p ON p.PlayerId = tm.PlayerId");
            int count = 0;

            for (DatabaseRow row : result.getRows()) {
                UUID townId = UUID.fromString(row.getString("TownId"));
                Town town = getTown(townId);
                if (town != null && town.loadMemberFromDB(row)) {
                    count++;
                }
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Town Members", count);
        }
    }

    /**
     * Проверяет данные городов, загруженных из базы данных.
     */
    public static void validate() {
        CivLogger.log(Level.INFO, "Validating data of loaded towns...");
        {
            Iterator<Town> it = towns.values().iterator();
            while (it.hasNext()) {
                Town town = it.next();
                if (town != null && !town.validate()) {
                    it.remove();
                }
            }

            CivLogger.log(Level.INFO, ">> Validated data of loaded towns");
        }
    }
}
