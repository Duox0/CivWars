package ru.civwars.listeners.world;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkLoadEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class ChunkLoadHandler extends BasicHandler<ChunkLoadEvent> {

    public ChunkLoadHandler(@NotNull CivWars plugin, @NotNull Class<ChunkLoadEvent> eventClass) {
        super(plugin, eventClass);
    }

    @Override
    protected void handle(@NotNull ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        CivWorld world = WorldManager.getWorld(chunk.getWorld());
        Entity[] entities = chunk.getEntities();
        

        // load mobs
        for(Entity entity : entities) {
            if(entity.hasMetadata("custom_mob")) {
                entity.remove();
            }
        }
    }

}
