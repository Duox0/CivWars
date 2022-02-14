package ru.civwars.listeners.block;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.building.Building;
import ru.civwars.building.BuildingLayer;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class PlaceHandler extends BasicHandler<BlockPlaceEvent> {

    public PlaceHandler(@NotNull CivWars civcraft) {
        super(civcraft, BlockPlaceEvent.class);
    }

    @Override
    protected void handle(@NotNull BlockPlaceEvent event) {
        KPlayer player = PlayerManager.getPlayer(event.getPlayer());
        Block block = event.getBlock();
        BlockPos pos = new BlockPos(block);
        CivWorld world = WorldManager.getWorld(block.getWorld());
        ItemStack stack = event.getItemInHand();

        CustomItem item = CustomItems.get().fromItemStack(stack);
        if (item != null) {
            event.setCancelled(true);
            return;
        }

        BuildingBlock buildingBlock = world.getBuildingBlock(pos);
        if (buildingBlock != null) {
            event.setCancelled(true);
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
                double lastBlocksPercent;
                if (layer.getTotalBlocksCount() <= 0) {
                    lastBlocksPercent = 0.0D;
                } else {
                    lastBlocksPercent = ((double) layer.getReinforcement() / (double) layer.getTotalBlocksCount());
                }

                layer.changeReinforcement(BlockUtils.getReinforcementValue(block.getTypeId()));
                if (lastBlocksPercent < BlockUtils.getReinforcementForLevel(0)) {
                    double newBlocksPercent;
                    if (layer.getTotalBlocksCount() <= 0) {
                        newBlocksPercent = 0.0D;
                    } else {
                        newBlocksPercent = ((double) layer.getReinforcement() / (double) layer.getTotalBlocksCount());
                    }

                    if (newBlocksPercent >= BlockUtils.getReinforcementForLevel(0)) {
                        player.sendRawMessage("Фундамент восстановлен для структуры: " + building.getName());
                    }
                }
            }
        }
    }
}
