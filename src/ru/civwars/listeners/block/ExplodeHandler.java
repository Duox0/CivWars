package ru.civwars.listeners.block;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import ru.civwars.CivWars;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class ExplodeHandler extends BasicHandler<BlockExplodeEvent> {

    public ExplodeHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockExplodeEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockExplodeEvent event) {
        Block block = event.getBlock();
        BlockPos pos = new BlockPos(block);
        CivWorld world = WorldManager.getWorld(block.getWorld());

        BuildingBlock buildingBlock = world.getBuildingBlock(pos);
        if (buildingBlock != null) {
            event.setCancelled(true);
        }

        if (event.isCancelled()) {
            return;
        }
        
        List<Block> blockList = Lists.newArrayList(event.blockList());
        for (Block block2 : blockList) {
            BlockPos pos2 = new BlockPos(block2);
            BuildingBlock buildingBlock2 = world.getBuildingBlock(pos2);
            if (buildingBlock2 != null) {
                event.blockList().remove(block2);
            }
        }
    }
}
