package ru.civwars.listeners.block;

import org.bukkit.event.block.EntityBlockFormEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class EntityBlockFormHandler extends BasicHandler<EntityBlockFormEvent> {

    public EntityBlockFormHandler(@NotNull CivWars civcraft) {
        super(civcraft, EntityBlockFormEvent.class);
    }

    @Override
    protected void handle(@NotNull EntityBlockFormEvent event) {
    }
}