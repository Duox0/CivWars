package ru.civwars.listeners.world;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkUnloadEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.BasicEntity;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class ChunkUnloadHandler extends BasicHandler<ChunkUnloadEvent> {

    public ChunkUnloadHandler(@NotNull CivWars plugin, @NotNull Class<ChunkUnloadEvent> eventClass) {
        super(plugin, eventClass);
    }

    @Override
    protected void handle(@NotNull ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        CivWorld world = WorldManager.getWorld(chunk.getWorld());
        Entity[] entities = chunk.getEntities();

        // unload mobs
        for(Entity entity : entities) {
            BasicEntity mob = world.getEntity(entity);
            if(mob != null) {
                mob.despawn();
            }
        }
    }

}
