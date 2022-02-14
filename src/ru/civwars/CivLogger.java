package ru.civwars;

import java.util.logging.Level;
import ru.lib27.annotation.NotNull;

public class CivLogger {

    private static CivWars plugin;

    public static void init(@NotNull CivWars plugin) {
        if (CivLogger.plugin != null) {
            return;
        }
        CivLogger.plugin = plugin;
    }
    
    private CivLogger() {

    }

    public static void log(@NotNull Level level, String message, Object object) {
        CivLogger.plugin.getLogger().log(level, message, object);
    }

    public static void log(@NotNull Level level, String message, Object... objects) {
        CivLogger.plugin.getLogger().log(level, message, objects);
    }

    public static void log(@NotNull Level level, String message, Throwable thrwbl) {
        CivLogger.plugin.getLogger().log(level, message, thrwbl);
    }
    
    public static void info(String message, Object... objects) {
        CivLogger.plugin.getLogger().log(Level.INFO, message, objects);
    }
}
