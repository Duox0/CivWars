package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import ru.civwars.CivWars;
import ru.civwars.listeners.player.*;
import ru.lib27.annotation.NotNull;

public class PlayerListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new PlayerListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public PlayerListener(@NotNull CivWars plugin) {
        super(plugin);
        
        this.registerHandler(new PlayerExpChangeHandler(plugin));
        
        this.registerHandler(new PlayerInteractEntityHandler(plugin));
        this.registerHandler(new PlayerInteractHandler(plugin));
        this.registerHandler(new PlayerSwapHandItemsHandler(plugin));
    }
    
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        this.handle0(event);
    }
    
}
