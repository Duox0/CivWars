package ru.civwars.listeners.block;

import org.bukkit.event.block.BlockSpreadEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.lib27.annotation.NotNull;

public class SpreadHandler extends BasicHandler<BlockSpreadEvent> {

    public SpreadHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockSpreadEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockSpreadEvent event) {
    }
}