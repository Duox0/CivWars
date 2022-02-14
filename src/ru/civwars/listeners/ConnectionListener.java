package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.civwars.CivWars;
import ru.civwars.entity.player.PlayerManager;
import ru.lib27.annotation.NotNull;

public class ConnectionListener implements Listener {

    private static Listener instance;

    public static void init(@NotNull CivWars plugin) {
        if (instance == null) {
            instance = new ConnectionListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }

    private final CivWars civcraft;

    public ConnectionListener(@NotNull CivWars civcraft) {
        this.civcraft = civcraft;
    }

    @EventHandler
    public void onPlayerConnection(PlayerJoinEvent event) {
        PlayerManager.onConnection(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnection(PlayerQuitEvent event) {
        PlayerManager.onDisconnection(event.getPlayer());
    }
}
