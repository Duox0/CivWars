package ru.civwars.entity.player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.database.Database;
import ru.databaseapi.database.DatabaseResult;
import ru.databaseapi.database.DatabaseRow;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PlayerCache {

    private static CivWars plugin;

    private static final ConcurrentHashMap<UUID, PlayerCacheEntry> cache = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if(PlayerCache.plugin != null) {
            return;
        }
        PlayerCache.plugin = plugin;
    }

    private PlayerCache() {
    }

    public static void load() {
        cache.clear();
        CivLogger.log(Level.INFO, "Loading player cache...");

        DatabaseResult result = Database.getConnection().query("SELECT PlayerId, Name, LastLogoutDate FROM players");
        for (DatabaseRow row : result.getRows()) {
            addPlayerCacheEntry(row);
        }

        CivLogger.log(Level.INFO, "Loaded {0} Player Infos", cache.size());
    }

    public static void addPlayerCacheEntry(@NotNull KPlayer player) {
        PlayerCacheEntry entry = new PlayerCacheEntry(player.getObjectId(), player.getName());
        cache.put(entry.getId(), entry);
    }
    
    private static void addPlayerCacheEntry(@NotNull DatabaseRow row) {
        PlayerCacheEntry entry = new PlayerCacheEntry(UUID.fromString(row.getString("PlayerId")), row.getString("Name"));
        entry.setLastLogoutTime(row.getLong("LastLogoutDate"));
        cache.put(entry.getId(), entry);
    }

    public static void updatePlayerData(@NotNull KPlayer player) {
        PlayerCacheEntry entry = cache.get(player.getObjectId());
        if (entry != null) {
            entry.setName(player.getName());
            entry.setLastLogoutTime(player.getLastLogoutTime());
        }
    }
    
    public static void updatePlayerName(@NotNull UUID id, @NotNull String name) {
        PlayerCacheEntry entry = cache.get(id);
        if (entry != null) {
            entry.setName(name);
        }
    }

    public static void updatePlayerTown(@NotNull UUID id, @Nullable UUID townId) {
        PlayerCacheEntry entry = cache.get(id);
        if (entry != null) {
            entry.setTownId(townId);
        }
    }
    
    public static PlayerCacheEntry get(@NotNull UUID id) {
        return cache.get(id);
    }
}
