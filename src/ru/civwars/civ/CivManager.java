package ru.civwars.civ;

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

public class CivManager {

    private static CivWars plugin;
    
    private static final ConcurrentHashMap<UUID, Civilization> civs = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if (CivManager.plugin != null) {
            return;
        }
        CivManager.plugin = plugin;
        
        new TableBuilder("civilizations",
                new TableColumn("CivId", ColumnType.UUID).primary(true),
                new TableColumn("Name", ColumnType.VARCHAR_64),
                new TableColumn("LeaderId", ColumnType.UUID).defaultValue("''"),
                new TableColumn("CapitalId", ColumnType.VARCHAR_64).defaultValue("''"),
                new TableColumn("Gold", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("CreatedDate", ColumnType.BIGINT).defaultValue(0)
        ).create(Database.getConnection());

        new TableBuilder("civilization_ranks",
                new TableColumn("CivId", ColumnType.UUID),
                new TableColumn("RankId", ColumnType.INT),
                new TableColumn("Name", ColumnType.VARCHAR_32),
                new TableColumn("Rights", ColumnType.BIGINT)
        ).create(Database.getConnection());
    }

    private CivManager() {
    }

    /**
     * Добавляет цивилизацию.
     *
     * @param civ цивилизация.
     */
    public static final void addCiv(@NotNull Civilization civ) {
        civs.put(civ.getId(), civ);
    }

    /**
     * Удаляет цивилизацию.
     *
     * @param civ цивилизация.
     */
    public static final void removeCiv(@NotNull Civilization civ) {
        civs.remove(civ.getId());
    }

    /**
     * Получает цивилизацию по идентификатору.
     * @param civId идетификатор цивилизации.
     * @return цивилизация или {@code null}, если не найден.
     */
    @Nullable
    public static Civilization getCiv(@NotNull UUID civId) {
        return civs.get(civId);
    }

    /**
     * Получает цивилизацию по имени.
     * @param name имя цивилизации.
     * @return цивилизация или {@code null}, если не найдено.
     */
    @Nullable
    public static Civilization getCiv(@NotNull String name) {
        for (Civilization c : civs.values()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Получает список всех цивилизаций.
     * @return список цивилизаций.
     */
    @NotNull
    public static Collection<Civilization> getCivs() {
        return civs.values();
    }

    /**
     * Загружает цивилизации из базы данных.
     */
    public static void load() {
        // ============================ Load all civilizations ============================
        CivLogger.log(Level.INFO, "Loading civilizations...");
        {
            DatabaseResult result = Database.getConnection().query("SELECT * FROM civilizations");
            
            int count = 0;
            
            for (DatabaseRow row : result.getRows()) {
                UUID civId = UUID.fromString(row.getString("CivId"));
                Civilization civ = new Civilization(civId);
                if (!civ.loadFromDB(row)) {
                    Database.getConnection().query("DELETE FROM civilizations WHERE CivId=?", civId);
                    continue;
                }

                addCiv(civ);
                count++;
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Civilizations", count);
        }
        
        

        // ============================ Load all civilization ranks ============================
        CivLogger.log(Level.INFO, "Loading civilization ranks...");
        {
            Database.getConnection().execute("DELETE cr FROM civilization_ranks cr LEFT JOIN civilizations c ON cr.CivId = c.CivId WHERE c.CivId IS NULL");

            DatabaseResult result = Database.getConnection().query("SELECT * FROM civilization_ranks");
            int count = 0;

            for (DatabaseRow row : result.getRows()) {
                UUID civId = UUID.fromString(row.getString("CivId"));
                Civilization civ = getCiv(civId);
                if (civ != null) {
                    civ.loadRankFromDB(row);
                    count++;
                }
            }

            CivLogger.log(Level.INFO, ">> Loaded {0} Civilization Ranks", count);
        }
    }
    
    /**
     * Проверяет данные цивилизаций, загруженных из базы данных.
     */
    public static void validate() {
        CivLogger.log(Level.INFO, "Validating data of loaded civilizations...");
        {
            Iterator<Civilization> it = civs.values().iterator();
            while (it.hasNext()) {
                Civilization civ = it.next();
                if (civ != null && !civ.validate()) {
                    it.remove();
                }
            }

            CivLogger.log(Level.INFO, ">> Validated data of loaded civilizations");
        }
    }

}
