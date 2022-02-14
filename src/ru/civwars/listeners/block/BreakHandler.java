package ru.civwars.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import ru.civwars.CivWars;
import ru.civwars.building.Building;
import ru.civwars.building.BuildingLayer;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class BreakHandler extends BasicHandler<BlockBreakEvent> {

    public BreakHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockBreakEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockBreakEvent event) {
        KPlayer player = PlayerManager.getPlayer(event.getPlayer());
        Block block = event.getBlock();
        BlockPos pos = new BlockPos(block);
        CivWorld world = WorldManager.getWorld(block.getWorld());

        BuildingBlock buildingBlock = world.getBuildingBlock(pos);
        if (buildingBlock != null) {
            buildingBlock.damage(world, player, 1);
            event.setCancelled(true);
            return;
        }

        Building building = world.getBuildingAtChunk(block.getChunk());
        if (building != null && pos.getY() < building.getY()) {
            if (!building.isValidated()) {
                event.setCancelled(true);
                return;
            }

            BuildingLayer layer = building.getLayer(pos.getY());
            if (layer != null) {
                /* Update the layer. */
                double blocksPercent;
                if(layer.getTotalBlocksCount() <= 0) {
                    blocksPercent = 0.0D;
                } else {
                    blocksPercent = ((double) layer.getReinforcement() / (double) layer.getTotalBlocksCount());
                }

                if (blocksPercent < BlockUtils.getReinforcementForLevel(0)) {
                    // Нельзя ломать этот блок, он поддерживает структуру: 
                    player.sendRawMessage("Нельзя ломать этот блок, он поддерживает структуру: " + building.getName());
                    event.setCancelled(true);
                    return;
                }
                layer.changeReinforcement(-BlockUtils.getReinforcementValue(block.getTypeId()));
            }
        }
    }
}
