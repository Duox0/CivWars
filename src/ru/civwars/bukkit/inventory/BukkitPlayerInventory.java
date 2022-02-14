package ru.civwars.bukkit.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import ru.civwars.TaskMaster;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class BukkitPlayerInventory extends BukkitInventory {

    @Override
    protected void processPlaceItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();
        BukkitItemSlot equipmentSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (equipmentSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(equipmentSlot);
            });
        }
    }

    @Override
    protected void processPickupItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        BukkitItemSlot equipmentSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (equipmentSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(equipmentSlot);
            });
        }
    }

    @Override
    protected void processSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        BukkitItemSlot clickedSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (clickedSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(clickedSlot);
            });
        }
    }

    @Override
    protected void processDropItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack cursorItem = event.getCursor();
        BukkitItemSlot clickedSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (clickedSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(clickedSlot);
            });
        }
    }

    @Override
    protected void processShiftClick(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        BukkitItemSlot clickedSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (clickedSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(clickedSlot);
            });
        }
    }

    @Override
    protected void processHotbarSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack hotbar = event.getView().getBottomInventory().getItem(event.getHotbarButton());

        BukkitItemSlot clickedSlot = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), event.getSlotType(), event.getSlot(), player.getEntity());
        if (clickedSlot != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(clickedSlot);
            });
        }

        BukkitItemSlot clickedSlot2 = BukkitInventory.getEquipmentSlot(event.getClickedInventory().getType(), InventoryType.SlotType.QUICKBAR, event.getHotbarButton(), player.getEntity());
        if (clickedSlot2 != null) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(clickedSlot2);
            });
        }
    }

}
