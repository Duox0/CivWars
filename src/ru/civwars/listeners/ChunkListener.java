package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.world.ChunkLoadHandler;
import ru.civwars.listeners.world.ChunkUnloadHandler;
import ru.lib27.annotation.NotNull;

public class ChunkListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new ChunkListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public ChunkListener(@NotNull CivWars plugin) {
        super(plugin);
        
        this.registerHandler(new ChunkLoadHandler(plugin, ChunkLoadEvent.class));
        this.registerHandler(new ChunkUnloadHandler(plugin, ChunkUnloadEvent.class));
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        this.handle0(event);
    }
    
}
