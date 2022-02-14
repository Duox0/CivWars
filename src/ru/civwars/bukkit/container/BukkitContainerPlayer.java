package ru.civwars.bukkit.container;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class BukkitContainerPlayer extends BukkitContainer {

    private final Inventory craftingInventory;
    private final Inventory playerInventory;
    
    public BukkitContainerPlayer(@NotNull InventoryView view) {
        super(view);
        this.craftingInventory = view.getTopInventory();
        this.playerInventory = view.getBottomInventory();
    }

    public List<Integer> shiftClick(@NotNull InventoryClickEvent event) {
        List<Integer> slots = Lists.newArrayList();

        int rawSlot = event.getRawSlot();
        ItemStack stackInSlot = event.getCurrentItem();
        if (!ItemUtils.isEmpty(stackInSlot)) {
            BukkitItemSlot equipmentSlot = ItemUtils.getSlotFromItemStack(stackInSlot);
            if (rawSlot == 0) {
                // result
                slots = this.a(stackInSlot, 9, 45, false);
            } else if (rawSlot >= 1 && rawSlot < 5) {
                slots = this.a(stackInSlot, 9, 45, false);
            } else if (rawSlot >= 5 && rawSlot < 9) {
                slots = this.a(stackInSlot, 9, 45, false);
            }
        }
        
        return slots;

        /**
         * 0
         * 1 4 6 8
         * 2 5 7
         * 3 45
         *
         * 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
         * 32 33 34 35 36 37 38 39 40 41 42 43 44
         */
       /* ItemStack itemstack = ItemStack.a;
        final Slot slot = this.slots.get(slot);
        if (slot != null && slot.hasItem()) {
            final ItemStack itemstack2 = slot.getItem();
            itemstack = itemstack2.cloneItemStack();
            final EnumItemSlot enumitemslot = EntityInsentient.d(itemstack);
            if (slot == 0) {
                if (!this.a(itemstack2, 9, 45, true)) {
                    return ItemStack.a;
                }
                slot.a(itemstack2, itemstack);
            } else if (slot >= 1 && slot < 5) {
                if (!this.a(itemstack2, 9, 45, false)) {
                    return ItemStack.a;
                }
            } else if (slot >= 5 && slot < 9) {
                if (!this.a(itemstack2, 9, 45, false)) {
                    return ItemStack.a;
                }
            } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR && !this.slots.get(8 - enumitemslot.b()).hasItem()) {
                final int j = 8 - enumitemslot.b();
                if (!this.a(itemstack2, j, j + 1, false)) {
                    return ItemStack.a;
                }
            } else if (enumitemslot == EnumItemSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                if (!this.a(itemstack2, 45, 46, false)) {
                    return ItemStack.a;
                }
            } else if (slot >= 9 && slot < 36) {
                if (!this.a(itemstack2, 36, 45, false)) {
                    return ItemStack.a;
                }
            } else if (slot >= 36 && slot < 45) {
                if (!this.a(itemstack2, 9, 36, false)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack2, 9, 45, false)) {
                return ItemStack.a;
            }
            if (itemstack2.isEmpty()) {
                slot.set(ItemStack.a);
            } else {
                slot.f();
            }
            if (itemstack2.getCount() == itemstack.getCount()) {
                return ItemStack.a;
            }
            final ItemStack itemstack3 = slot.a(entityhuman, itemstack2);
            if (slot == 0) {
                entityhuman.drop(itemstack3, false);
            }
        }
        return itemstack;*/
    }
}
