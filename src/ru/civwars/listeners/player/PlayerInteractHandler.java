package ru.civwars.listeners.player;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.bukkit.item.BukkitItem;
import ru.civwars.bukkit.item.BukkitItems;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.EnumInteractionResult;
import ru.civwars.util.InteractionResult;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class PlayerInteractHandler extends BasicHandler<PlayerInteractEvent> {

    public PlayerInteractHandler(@NotNull CivWars plugin) {
        super(plugin, PlayerInteractEvent.class);
    }

    @Override
    protected void handle(@NotNull PlayerInteractEvent event) {
        KPlayer player = this.getPlayer(event.getPlayer());

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_BLOCK:
                this.processTryUseItemOnBlock(player, event);
                break;
            case LEFT_CLICK_AIR:
                break;
            case RIGHT_CLICK_AIR:
                this.processTryUseItem(player, event);
                break;
            case PHYSICAL:
                break;
        }
    }

    private void processTryUseItemOnBlock(@NotNull KPlayer player, @NotNull PlayerInteractEvent event) {
        BukkitItemSlot hand = BukkitItemSlot.getEquipmentSlot(event.getHand());
        ItemStack stack = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        BlockFace facing = event.getBlockFace();

        EnumInteractionResult result = this.processRightClickBlock(player, stack, hand, clickedBlock, facing, event.useItemInHand(), event.useInteractedBlock());
        if (result == EnumInteractionResult.PASS) {
            if (!ItemUtils.isEmpty(stack)) {
                result = this.processRightClick(player, stack, hand);
                if (result == EnumInteractionResult.PASS) {
                    BukkitItem bukkitItem = BukkitItems.getBukkitItem(stack.getType());
                    if (bukkitItem != null) {
                        bukkitItem.onUseItemRightClick(stack, player, hand);
                    }
                }
            }
        }

        if (result != EnumInteractionResult.PASS) {
            event.setCancelled(true);
        }
    }

    private void processTryUseItem(@NotNull KPlayer player, @NotNull PlayerInteractEvent event) {
        BukkitItemSlot hand = BukkitItemSlot.getEquipmentSlot(event.getHand());
        ItemStack stack = event.getItem();

        if (!ItemUtils.isEmpty(stack)) {
            EnumInteractionResult result = this.processRightClick(player, stack, hand);
            if (result != EnumInteractionResult.PASS) {
                event.setCancelled(true);
                return;
            }

            BukkitItem bukkitItem = BukkitItems.getBukkitItem(stack.getType());
            if (bukkitItem != null) {
                bukkitItem.onUseItemRightClick(stack, player, hand);
            }
        }
    }

    private EnumInteractionResult processRightClickBlock(@NotNull KPlayer player, ItemStack stack, @NotNull BukkitItemSlot hand, @NotNull Block clickedBlock, @NotNull BlockFace facing, @NotNull Event.Result useItemInHand, @NotNull Event.Result useClickedBlock) {
        System.out.println("processRightClickBlock");

        EnumInteractionResult enumResult = EnumInteractionResult.PASS;

        if (!player.getEntity().isSneaking() || useClickedBlock == Event.Result.ALLOW) {
            if (useClickedBlock != Event.Result.DENY && clickedBlock.getType() == Material.CHEST) {
                enumResult = EnumInteractionResult.SUCCESS;
            }
        }

        if (ItemUtils.isEmpty(stack)) {
            return EnumInteractionResult.PASS;
        }

        if ((enumResult != EnumInteractionResult.SUCCESS && useItemInHand != Event.Result.DENY) || (enumResult == EnumInteractionResult.SUCCESS && useItemInHand == Event.Result.ALLOW)) {
            CustomItem customItem = CustomItems.get().fromItemStack(stack);
            if (customItem != null) {
                InteractionResult<org.bukkit.inventory.ItemStack> result = customItem.useItemRightClickBlock(stack.clone(), player, clickedBlock, facing);
                enumResult = result.getType();
                if (enumResult != EnumInteractionResult.PASS) {
                    if (enumResult == EnumInteractionResult.SUCCESS) {
                        ItemStack resultStack = result.getResult();
                        if (!ItemUtils.isEmpty(resultStack)) {
                            player.setHeldItem(hand, resultStack);
                        } else {
                            // destroy item
                            player.setHeldItem(hand, null);
                        }
                    }
                }
            }
        }

        return enumResult;
    }

    private EnumInteractionResult processRightClick(@NotNull KPlayer player, ItemStack stack, @NotNull BukkitItemSlot hand) {
        System.out.println("processRightClick");
        CustomItem customItem = CustomItems.get().fromItemStack(stack);
        EnumInteractionResult enumResult = EnumInteractionResult.PASS;

        if (customItem != null) {
            InteractionResult<org.bukkit.inventory.ItemStack> result = customItem.useItemRightClick(stack.clone(), player);
            enumResult = result.getType();
            if (enumResult != EnumInteractionResult.PASS) {
                if (enumResult == EnumInteractionResult.SUCCESS) {
                    ItemStack resultStack = result.getResult();
                    if (!ItemUtils.isEmpty(resultStack)) {
                        player.setHeldItem(hand, resultStack);
                    } else {
                        // destroy item
                        player.setHeldItem(hand, null);
                    }
                }
            }
        }

        return enumResult;
    }

}
