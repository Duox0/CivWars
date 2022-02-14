package ru.civwars.database;

import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.Config;
import ru.databaseapi.DatabaseAPI;
import ru.databaseapi.database.DatabaseConnection;
import ru.lib27.annotation.NotNull;

public class Database {

    private static CivWars plugin;

    private static DatabaseConnection connection;

    public static void init(@NotNull CivWars plugin) {
        if (Database.plugin != null) {
            return;
        }
        Database.plugin = plugin;
        
        try {
            connection = DatabaseAPI.getConnection(plugin, Config.DATABASE_HOST, Config.DATABASE_PORT, Config.DATABASE_USER, Config.DATABASE_PASSWORD, Config.DATABASE);
        } catch (Exception ex) {
            CivLogger.log(Level.SEVERE, "Could not initialize MySQL connection to database {0}", Config.DATABASE);
            ex.printStackTrace();
        }
    }

    private Database() {
    }

    @NotNull
    public static DatabaseConnection getConnection() {
        return connection;
    }

}
