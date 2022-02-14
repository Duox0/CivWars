package ru.civwars.listeners.block;

import org.bukkit.event.block.BlockDispenseEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class DispenseHandler extends BasicHandler<BlockDispenseEvent> {

    public DispenseHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockDispenseEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockDispenseEvent event) {
    }
}