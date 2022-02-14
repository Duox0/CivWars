package ru.civwars;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    /* Debug mode. */
    public static boolean DEBUG;

    // ========================================================================
    // =============================== Database ===============================
    // ========================================================================
    // Доступ к базе данных.
    /* Host адрес для доступа к базе данных. */
    public static String DATABASE_HOST;
    /* Порт для доступа к базе данных. */
    public static String DATABASE_PORT;
    /* Логин для доступа к базе данных. */
    public static String DATABASE_USER;
    /* Пароль для доступа к базе данных. */
    public static String DATABASE_PASSWORD;
    /* Имя базы данных. */
    public static String DATABASE;
    /* Максимальное количество подключений к базе данных. */
    public static int DATABASE_MAX_CONNECTIONS;

    // Thread pools size
    /* General thread pool size. */
    public static int GENERAL_THREAD_POOL_SIZE;
    
    // Конфигурационные файлы.
    /* Файл свойств для конфигураций сервера. */
    public static final String CONFIGURATION_FILE = "config/server.properties";
    public static final String OPTIONS_FILE = "config/options.properties";

    private Config() {

    }

    public static void load() {
        CivLogger.info("Loading config");
        
        DEBUG = true;
        try {
            Properties serverSettings = new Properties();
            InputStream is = new FileInputStream(new File(CivWars.get().getDataFolder(), CONFIGURATION_FILE));
            serverSettings.load(is);
            is.close();

            DATABASE_HOST = serverSettings.getProperty("Host", "localhost");
            DATABASE_PORT = serverSettings.getProperty("Port", "3306");
            DATABASE_USER = serverSettings.getProperty("User", "root");
            DATABASE_PASSWORD = serverSettings.getProperty("Password", "root");
            DATABASE = serverSettings.getProperty("Database", "civwars_db");
            DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("Failed to Load " + CONFIGURATION_FILE + " File.");
        }
        
        try {
            Properties optionsSettings = new Properties();
            InputStream is = new FileInputStream(new File(CivWars.get().getDataFolder(), OPTIONS_FILE));
            optionsSettings.load(is);
            is.close();

            GENERAL_THREAD_POOL_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralThreadPoolSize", "2"));

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("Failed to Load " + OPTIONS_FILE + " File.");
        }
    }

}
