package ru.civwars.listeners.block;

import org.bukkit.event.block.BlockFromToEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class FromToHandler extends BasicHandler<BlockFromToEvent> {

    public FromToHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockFromToEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockFromToEvent event) {
    }
}