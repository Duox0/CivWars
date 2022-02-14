package ru.civwars.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockFormEvent;
import ru.civwars.CivWars;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class FormHandler extends BasicHandler<BlockFormEvent> {

    public FormHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockFormEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockFormEvent event) {
        Block block = event.getBlock();
        BlockPos pos = new BlockPos(block);
        CivWorld world = WorldManager.getWorld(block.getWorld());
        
        BuildingBlock buildingBlock = world.getBuildingBlock(pos);
        if(buildingBlock != null) {
            event.setCancelled(true);
        }
    }
}