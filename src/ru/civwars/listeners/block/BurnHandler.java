package ru.civwars.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBurnEvent;
import ru.civwars.CivWars;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class BurnHandler extends BasicHandler<BlockBurnEvent> {

    public BurnHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockBurnEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockBurnEvent event) {
        Block block = event.getBlock();
        BlockPos pos = new BlockPos(block);
        CivWorld world = WorldManager.getWorld(block.getWorld());
        
        BuildingBlock buildingBlock = world.getBuildingBlock(pos);
        if(buildingBlock != null) {
            event.setCancelled(true);
        }
    }
}