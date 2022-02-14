package ru.civwars.entity.player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import ru.civwars.CivWars;
import ru.civwars.CivLogger;
import ru.civwars.database.Database;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.town.TownMember;
import ru.databaseapi.database.DatabaseRow;
import ru.databaseapi.database.table.ColumnType;
import ru.databaseapi.database.table.TableBuilder;
import ru.databaseapi.database.table.TableColumn;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PlayerManager {

    private static CivWars plugin;
    
    private static final ConcurrentHashMap<UUID, KPlayer> players = new ConcurrentHashMap<>();

    public static void init(@NotNull CivWars plugin) {
        if (PlayerManager.plugin != null) {
            return;
        }
        PlayerManager.plugin = plugin;
        
        new TableBuilder("players",
                new TableColumn("PlayerId", ColumnType.UUID).primary(true),
                new TableColumn("Name", ColumnType.VARCHAR_64),
                new TableColumn("Gold", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("CreatedDate", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("LastLoginDate", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("LastLogoutDate", ColumnType.BIGINT).defaultValue(0),
                new TableColumn("LastLoginAddress", ColumnType.VARCHAR_15)
        ).create(Database.getConnection());
    }

    private PlayerManager() {
    }

    /**
     * Получает игрока по идентификатору.
     *
     * @param playerId идентификатор игрока.
     * @return игрок или {@code null}, если игрок не найден.
     */
    @Nullable
    public static KPlayer getPlayer(@NotNull UUID playerId) {
        return players.get(playerId);
    }

    /**
     * Получает игрока по имени.
     *
     * @param name имя игрока.
     * @return игрок или {@code null}, если игрок не найден.
     */
    @Nullable
    public static KPlayer getPlayer(@NotNull String name) {
        for (KPlayer p : players.values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Получает игрока по объекту Bukkit-игрока.
     *
     * @param player Bukkit-объект игрока.
     * @return игрок или {@code null}, если не найден.
     */
    @NotNull
    public static KPlayer getPlayer(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Получает список всех игроков, которые находятся в игре.
     *
     * @return список игроков.
     */
    @NotNull
    public static Collection<KPlayer> getPlayers() {
        return players.values();
    }

    public static void onConnection(@NotNull Player player) {
        try {
            DatabaseRow row = Database.getConnection().query("SELECT * FROM players WHERE PlayerId=? LIMIT 1", player.getUniqueId()).first();

            final KPlayer kPlayer = new KPlayer(player);
            if (row != null) {
                kPlayer.loadFromDB(row);
            } else if (!kPlayer.create()) {
                player.kickPlayer("Error loading player data!");
                return;
            } else {
                PlayerCache.addPlayerCacheEntry(kPlayer);
            }
            
            kPlayer.init();
            PlayerManager.players.put(player.getUniqueId(), kPlayer);
            kPlayer.onConnection();
            
            try {
                row = Database.getConnection().query("SELECT TownId FROM town_members WHERE PlayerId=? LIMIT 1", kPlayer.getObjectId()).first();
                if (row != null) {
                    UUID townId = UUID.fromString(row.getString("TownId"));
                    Town town = townId != null ? TownManager.getTown(townId) : null;
                    if (town != null) {
                        TownMember member = town.getMember(kPlayer.getObjectId());
                        if (member != null) {
                            kPlayer.setTown(town);
                            member.onConnection(kPlayer);
                        }
                    } else {
                        Database.getConnection().asyncExecute("DELETE FROM town_members WHERE PlayerId=?", kPlayer.getObjectId());
                    }
                }
            } catch (Exception ex) {
                CivLogger.log(Level.SEVERE, "Error loading town member data: ", ex);
            }
            
            PlayerCache.updatePlayerData(kPlayer);
        } catch (Exception ex) {
            CivLogger.log(Level.SEVERE, null, ex);
            player.kickPlayer("Error loading player data!");
        }
    }

    public static void onDisconnection(@NotNull Player player) {
        KPlayer fplayer = PlayerManager.players.remove(player.getUniqueId());
        if (fplayer == null) {
            return;
        }

        if (fplayer.getTown() != null) {
            TownMember member = fplayer.getTown().getMember(fplayer.getObjectId());
            if (member != null) {
                member.onDisconnection();
            }
        }
        fplayer.onDisconnection();
    }

}
