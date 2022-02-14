package ru.civwars.listeners.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.SlimeSplitEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class SlimeSplitHandler extends BasicHandler<SlimeSplitEvent> {

    public SlimeSplitHandler(@NotNull CivWars plugin, @NotNull Class<SlimeSplitEvent> eventClass) {
        super(plugin, eventClass);
    }

    @Override
    protected void handle(@NotNull SlimeSplitEvent event) {
        Entity entity = event.getEntity();
        if (!entity.hasMetadata("custom_mob")) {
            return;
        }

        event.setCount(0);
        event.setCancelled(true);
    }
}
