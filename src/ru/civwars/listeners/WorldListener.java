package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.*;
import ru.civwars.CivWars;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class WorldListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new WorldListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public WorldListener(@NotNull CivWars plugin) {
        super(plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldInitEvent event) {
        CivWorld world = new CivWorld(event.getWorld());
        WorldManager.addWorld(world);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldUnloadEvent event) {
        if(event.isCancelled()) {
            return;
        }
        CivWorld world = WorldManager.getWorld(event.getWorld());
        if(world != null) {
            WorldManager.removeWorld(world);
        }
    }
    
}
