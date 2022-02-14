package ru.civwars.listeners.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class EntityChangeBlockHandler extends BasicHandler<EntityChangeBlockEvent> {

    public EntityChangeBlockHandler(@NotNull CivWars plugin, @NotNull Class<EntityChangeBlockEvent> eventClass) {
        super(plugin, eventClass);
    }

    @Override
    protected void handle(@NotNull EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!entity.hasMetadata("custom_mob")) {
            return;
        }

        event.setCancelled(true);
    }
}
