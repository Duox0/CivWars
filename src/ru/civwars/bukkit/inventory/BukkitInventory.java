package ru.civwars.bukkit.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BukkitInventory {

    /*
    public ItemStack a(int slot, int button, final InventoryAction action, final EntityHuman entityhuman) {
        if (action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_SOME || action == InventoryAction.PICKUP_HALF || action == InventoryAction.PICKUP_ONE) {
            ItemStack clickedItem = event.getCurrentItem();
            
            if (slot < 0) {
                return ItemStack.a;
            }
            final Slot slot3 = this.slots.get(slot);
            ItemStack itemstack3 = slot3.getItem();
            final ItemStack itemstack2 = playerinventory.getCarried();
            if (!itemstack3.isEmpty()) {
                itemstack = itemstack3.cloneItemStack();
            }
            if (itemstack3.isEmpty()) {
                if (!itemstack2.isEmpty() && slot3.isAllowed(itemstack2)) {
                    int k2 = (button == 0) ? itemstack2.getCount() : 1;
                    if (k2 > slot3.getMaxStackSize(itemstack2)) {
                        k2 = slot3.getMaxStackSize(itemstack2);
                    }
                    slot3.set(itemstack2.cloneAndSubtract(k2));
                }
            } else if (slot3.isAllowed(entityhuman)) {
                if (itemstack2.isEmpty()) {
                    if (itemstack3.isEmpty()) {
                        slot3.set(ItemStack.a);
                        playerinventory.setCarried(ItemStack.a);
                    } else {
                        final int k2 = (button == 0) ? itemstack3.getCount() : ((itemstack3.getCount() + 1) / 2);
                        playerinventory.setCarried(slot3.a(k2));
                        if (itemstack3.isEmpty()) {
                            slot3.set(ItemStack.a);
                        }
                        slot3.a(entityhuman, playerinventory.getCarried());
                    }
                } else if (slot3.isAllowed(itemstack2)) {
                    if (itemstack3.getItem() == itemstack2.getItem() && itemstack3.getData() == itemstack2.getData() && ItemStack.equals(itemstack3, itemstack2)) {
                        int k2 = (button == 0) ? itemstack2.getCount() : 1;
                        if (k2 > slot3.getMaxStackSize(itemstack2) - itemstack3.getCount()) {
                            k2 = slot3.getMaxStackSize(itemstack2) - itemstack3.getCount();
                        }
                        if (k2 > itemstack2.getMaxStackSize() - itemstack3.getCount()) {
                            k2 = itemstack2.getMaxStackSize() - itemstack3.getCount();
                        }
                        itemstack2.subtract(k2);
                        itemstack3.add(k2);
                    } else if (itemstack2.getCount() <= slot3.getMaxStackSize(itemstack2)) {
                        slot3.set(itemstack2);
                        playerinventory.setCarried(itemstack3);
                    }
                } else if (itemstack3.getItem() == itemstack2.getItem() && itemstack2.getMaxStackSize() > 1 && (!itemstack3.usesData() || itemstack3.getData() == itemstack2.getData()) && ItemStack.equals(itemstack3, itemstack2) && !itemstack3.isEmpty()) {
                    final int k2 = itemstack3.getCount();
                    if (k2 + itemstack2.getCount() <= itemstack2.getMaxStackSize()) {
                        itemstack2.add(k2);
                        itemstack3 = slot3.a(k2);
                        if (itemstack3.isEmpty()) {
                            slot3.set(ItemStack.a);
                        }
                        slot3.a(entityhuman, playerinventory.getCarried());
                    }
                }
            }
            slot3.f();
            if (entityhuman instanceof EntityPlayer && slot3.getMaxStackSize() != 64) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, slot3.rawSlotIndex, slot3.getItem()));
                if (this.getBukkitView().getType() == InventoryType.WORKBENCH || this.getBukkitView().getType() == InventoryType.CRAFTING) {
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, 0, this.getSlot(0).getItem()));
                }
            }
        } else if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_SOME || action == InventoryAction.PLACE_ONE) {
            ItemStack cursorItem = event.getCursor();
            
            final Slot slot3 = this.slots.get(slot);
            ItemStack itemstack3 = slot3.getItem();
            final ItemStack itemstack2 = playerinventory.getCarried();
            if (!itemstack2.isEmpty() && slot3.isAllowed(itemstack2)) {
                int k2 = (button == 0) ? itemstack2.getCount() : 1;
                if (k2 > slot3.getMaxStackSize(itemstack2)) {
                    k2 = slot3.getMaxStackSize(itemstack2);
                }
                slot3.set(itemstack2.cloneAndSubtract(k2));
            }
        }

        ItemStack itemstack = ItemStack.a;
        final PlayerInventory playerinventory = entityhuman.inventory;
        if ((action == InventoryClickType.PICKUP || action == InventoryClickType.QUICK_MOVE) && (button == 0 || button == 1)) {
            // MOVE_TO_OTHER_INVENTORY, DROP_ALL_CURSOR, DROP_ONE_CURSOR, PLACE_ALL, PLACE_ONE, PICKUP_ALL, PICKUP_HALF, PICKUP_SOME, PICKUP_ONE, PLACE_SOME, SWAP_WITH_CURSOR, PICKUP_ALL
            if (slot == -999) {
                if (!playerinventory.getCarried().isEmpty()) {
                    if (button == 0) {
                        final ItemStack carried = playerinventory.getCarried();
                        playerinventory.setCarried(ItemStack.a);
                        entityhuman.drop(carried, true);
                    }
                    if (button == 1) {
                        entityhuman.drop(playerinventory.getCarried().cloneAndSubtract(1), true);
                    }
                }
            } else if (action == InventoryClickType.QUICK_MOVE) {
                if (slot < 0) {
                    return ItemStack.a;
                }
                final Slot slot3 = this.slots.get(slot);
                if (slot3 == null || !slot3.isAllowed(entityhuman)) {
                    return ItemStack.a;
                }
                for (ItemStack itemstack3 = this.shiftClick(entityhuman, slot); !itemstack3.isEmpty(); itemstack3 = this.shiftClick(entityhuman, slot)) {
                    if (!ItemStack.c(slot3.getItem(), itemstack3)) {
                        break;
                    }
                    itemstack = itemstack3.cloneItemStack();
                }
            } else {
                if (slot < 0) {
                    return ItemStack.a;
                }
                final Slot slot3 = this.slots.get(slot);
                if (slot3 != null) {
                    ItemStack itemstack3 = slot3.getItem();
                    final ItemStack itemstack2 = playerinventory.getCarried();
                    if (!itemstack3.isEmpty()) {
                        itemstack = itemstack3.cloneItemStack();
                    }
                    if (itemstack3.isEmpty()) {
                        if (!itemstack2.isEmpty() && slot3.isAllowed(itemstack2)) {
                            int k2 = (button == 0) ? itemstack2.getCount() : 1;
                            if (k2 > slot3.getMaxStackSize(itemstack2)) {
                                k2 = slot3.getMaxStackSize(itemstack2);
                            }
                            slot3.set(itemstack2.cloneAndSubtract(k2));
                        }
                    } else if (slot3.isAllowed(entityhuman)) {
                        if (itemstack2.isEmpty()) {
                            if (itemstack3.isEmpty()) {
                                slot3.set(ItemStack.a);
                                playerinventory.setCarried(ItemStack.a);
                            } else {
                                final int k2 = (button == 0) ? itemstack3.getCount() : ((itemstack3.getCount() + 1) / 2);
                                playerinventory.setCarried(slot3.a(k2));
                                if (itemstack3.isEmpty()) {
                                    slot3.set(ItemStack.a);
                                }
                                slot3.a(entityhuman, playerinventory.getCarried());
                            }
                        } else if (slot3.isAllowed(itemstack2)) {
                            if (itemstack3.getItem() == itemstack2.getItem() && itemstack3.getData() == itemstack2.getData() && ItemStack.equals(itemstack3, itemstack2)) {
                                int k2 = (button == 0) ? itemstack2.getCount() : 1;
                                if (k2 > slot3.getMaxStackSize(itemstack2) - itemstack3.getCount()) {
                                    k2 = slot3.getMaxStackSize(itemstack2) - itemstack3.getCount();
                                }
                                if (k2 > itemstack2.getMaxStackSize() - itemstack3.getCount()) {
                                    k2 = itemstack2.getMaxStackSize() - itemstack3.getCount();
                                }
                                itemstack2.subtract(k2);
                                itemstack3.add(k2);
                            } else if (itemstack2.getCount() <= slot3.getMaxStackSize(itemstack2)) {
                                slot3.set(itemstack2);
                                playerinventory.setCarried(itemstack3);
                            }
                        } else if (itemstack3.getItem() == itemstack2.getItem() && itemstack2.getMaxStackSize() > 1 && (!itemstack3.usesData() || itemstack3.getData() == itemstack2.getData()) && ItemStack.equals(itemstack3, itemstack2) && !itemstack3.isEmpty()) {
                            final int k2 = itemstack3.getCount();
                            if (k2 + itemstack2.getCount() <= itemstack2.getMaxStackSize()) {
                                itemstack2.add(k2);
                                itemstack3 = slot3.a(k2);
                                if (itemstack3.isEmpty()) {
                                    slot3.set(ItemStack.a);
                                }
                                slot3.a(entityhuman, playerinventory.getCarried());
                            }
                        }
                    }
                    slot3.f();
                    if (entityhuman instanceof EntityPlayer && slot3.getMaxStackSize() != 64) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, slot3.rawSlotIndex, slot3.getItem()));
                        if (this.getBukkitView().getType() == InventoryType.WORKBENCH || this.getBukkitView().getType() == InventoryType.CRAFTING) {
                            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutSetSlot(this.windowId, 0, this.getSlot(0).getItem()));
                        }
                    }
                }
            }
        } else if (action == InventoryClickType.SWAP && button >= 0 && button < 9) {
            // HOTBAR_SWAP, HOTBAR_MOVE_AND_READD
            final Slot slot3 = this.slots.get(slot);
            final ItemStack itemstack3 = playerinventory.getItem(button);
            final ItemStack itemstack2 = slot3.getItem();
            if (!itemstack3.isEmpty() || !itemstack2.isEmpty()) {
                if (itemstack3.isEmpty()) {
                    if (slot3.isAllowed(entityhuman)) {
                        playerinventory.setItem(button, itemstack2);
                        slot3.b(itemstack2.getCount());
                        slot3.set(ItemStack.a);
                        slot3.a(entityhuman, itemstack2);
                    }
                } else if (itemstack2.isEmpty()) {
                    if (slot3.isAllowed(itemstack3)) {
                        final int k2 = slot3.getMaxStackSize(itemstack3);
                        if (itemstack3.getCount() > k2) {
                            slot3.set(itemstack3.cloneAndSubtract(k2));
                        } else {
                            slot3.set(itemstack3);
                            playerinventory.setItem(button, ItemStack.a);
                        }
                    }
                } else if (slot3.isAllowed(entityhuman) && slot3.isAllowed(itemstack3)) {
                    final int k2 = slot3.getMaxStackSize(itemstack3);
                    if (itemstack3.getCount() > k2) {
                        slot3.set(itemstack3.cloneAndSubtract(k2));
                        slot3.a(entityhuman, itemstack2);
                        if (!playerinventory.pickup(itemstack2)) {
                            entityhuman.drop(itemstack2, true);
                        }
                    } else {
                        slot3.set(itemstack3);
                        playerinventory.setItem(button, itemstack2);
                        slot3.a(entityhuman, itemstack2);
                    }
                }
            }
        } else if (action == InventoryClickType.CLONE && entityhuman.abilities.canInstantlyBuild && playerinventory.getCarried().isEmpty() && slot >= 0) {
            // CLONE_STACK
            final Slot slot3 = this.slots.get(slot);
            if (slot3 != null && slot3.hasItem()) {
                final ItemStack itemstack3 = slot3.getItem().cloneItemStack();
                itemstack3.setCount(itemstack3.getMaxStackSize());
                playerinventory.setCarried(itemstack3);
            }
        } else if (action == InventoryClickType.THROW && cursorItem.isEmpty() && slot >= 0) {
            // DROP_ONE_SLOT, DROP_ALL_SLOT
            final Slot slot3 = this.slots.get(slot);
            if (slot3 != null && slot3.hasItem() && slot3.isAllowed(entityhuman)) {
                final ItemStack itemstack3 = slot3.a((button == 0) ? 1 : slot3.getItem().getCount());
                slot3.a(entityhuman, itemstack3);
                entityhuman.drop(itemstack3, true);
            }
        } else if (action == InventoryClickType.PICKUP_ALL && slot >= 0) {
            InventoryAction.COLLECT_TO_CURSOR;
            final Slot slot3 = this.slots.get(slot);
            final ItemStack itemstack3 = playerinventory.getCarried();
            if (!itemstack3.isEmpty() && (slot3 == null || !slot3.hasItem() || !slot3.isAllowed(entityhuman))) {
                final int l = (j == 0) ? 0 : (this.slots.size() - 1);
                final int k2 = (j == 0) ? 1 : -1;
                for (int l2 = 0; l2 < 2; ++l2) {
                    for (int i3 = l; i3 >= 0 && i3 < this.slots.size() && itemstack3.getCount() < itemstack3.getMaxStackSize(); i3 += k2) {
                        final Slot slot4 = this.slots.get(i3);
                        if (slot4.hasItem() && a(slot4, itemstack3, true) && slot4.isAllowed(entityhuman) && this.a(itemstack3, slot4)) {
                            final ItemStack itemstack6 = slot4.getItem();
                            if (l2 != 0 || itemstack6.getCount() != itemstack6.getMaxStackSize()) {
                                final int k = Math.min(itemstack3.getMaxStackSize() - itemstack3.getCount(), itemstack6.getCount());
                                final ItemStack itemstack7 = slot4.a(k);
                                itemstack3.add(k);
                                if (itemstack7.isEmpty()) {
                                    slot4.set(ItemStack.a);
                                }
                                slot4.a(entityhuman, itemstack7);
                            }
                        }
                    }
                }
            }
            this.b();
        }
        return itemstack;
    }*/

    public final void processTryPlaceItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTryPlaceItem");
        ItemStack cursorItem = event.getCursor();
        this.processPlaceItem_(player, event);
    }

    protected void processPlaceItem_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processPlaceItem");
        this.processPlaceItem(player, event);
    }

    public final void processTryPickupItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTryPickupItem");
        ItemStack clickedItem = event.getCurrentItem();
        this.processPlaceItem_(player, event);
    }

    protected void processPickupItem_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processPickupItem");
        this.processPickupItem(player, event);
    }

    public final void processTrySwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTrySwapItems");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        this.processSwapItems_(player, event);
    }

    protected void processSwapItems_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processSwapItems");
        this.processSwapItems(player, event);
    }

    public final void processTryDropItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTryDropItem");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        this.processDropItem_(player, event);
    }

    protected void processDropItem_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processDropItem");
        this.processDropItem(player, event);
    }

    public final void processTryShiftClick(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTryShiftClick");
        ItemStack clickedItem = event.getCurrentItem();
        this.processShiftClick_(player, event);
    }

    protected void processShiftClick_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processShiftClick");
        this.processShiftClick(player, event);
    }

    public final void processTryHotbarSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processTryHotbarSwapItems");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack hotbar = event.getView().getBottomInventory().getItem(event.getHotbarButton());
        this.processHotbarSwapItems_(player, event);
    }

    protected void processHotbarSwapItems_(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("        processHotbarSwapItems");
        this.processHotbarSwapItems(player, event);
    }

    protected abstract void processPlaceItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

    protected abstract void processPickupItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

    protected abstract void processSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

    protected abstract void processDropItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

    protected abstract void processShiftClick(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

    protected abstract void processHotbarSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event);

   /* public void shiftClick(@NotNull InventoryClickEvent event) {
        ItemStack stackInSlot = event.getCurrentItem();
        if(!ItemUtils.isEmpty(stackInSlot)) {
            BukkitItemSlot equipmentSlot = ItemUtils.getSlotFromItemStack(stackInSlot);
            if(event.getRawSlot())
        }
        
        /**
         * 0
         * 1    4 6 8
         * 2    5 7
         * 3    45
         * 
         * 9 10 11 12 13 14 15 16 17
         * 18 19 20 21 22 23 24 25 26
         * 27 28 29 30 31 32 33 34 35
         * 36 37 38 39 40 41 42 43 44
         */
   /*     
        
        ItemStack itemstack = ItemStack.a;
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
        return itemstack;
    }

    protected boolean a(final ItemStack itemstack, final int i, final int j, final boolean flag) {
        itemstack = stackInSlot;
        i = 45;
        j = 46;
        flag = false;
        
        boolean flag2 = false;
        int k = i; //45
        if (flag) {
            k = j - 1;
        }
        if (itemstack.isStackable()) {
            while (!itemstack.isEmpty()) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                }
                else if (k >= j) {
                    break;
                }
                final Slot slot = this.slots.get(k);
                final ItemStack itemstack2 = slot.getItem();
                if (!itemstack2.isEmpty() && itemstack2.getItem() == itemstack.getItem() && (!itemstack.usesData() || itemstack.getData() == itemstack2.getData()) && ItemStack.equals(itemstack, itemstack2)) {
                    final int l = itemstack2.getCount() + itemstack.getCount();
                    if (l <= itemstack.getMaxStackSize()) {
                        itemstack.setCount(0);
                        itemstack2.setCount(l);
                        slot.f();
                        flag2 = true;
                    }
                    else if (itemstack2.getCount() < itemstack.getMaxStackSize()) {
                        itemstack.subtract(itemstack.getMaxStackSize() - itemstack2.getCount());
                        itemstack2.setCount(itemstack.getMaxStackSize());
                        slot.f();
                        flag2 = true;
                    }
                }
                if (flag) {
                    --k;
                }
                else {
                    ++k;
                }
            }
        }
        if (!itemstack.isEmpty()) {
            if (flag) {
                k = j - 1;
            }
            else {
                k = i;
            }
            while (true) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                }
                else if (k >= j) {
                    break;
                }
                final Slot slot = this.slots.get(k);
                final ItemStack itemstack2 = slot.getItem();
                if (itemstack2.isEmpty() && slot.isAllowed(itemstack)) {
                    if (itemstack.getCount() > slot.getMaxStackSize()) {
                        slot.set(itemstack.cloneAndSubtract(slot.getMaxStackSize()));
                    }
                    else {
                        slot.set(itemstack.cloneAndSubtract(itemstack.getCount()));
                    }
                    slot.f();
                    flag2 = true;
                    break;
                }
                if (flag) {
                    --k;
                }
                else {
                    ++k;
                }
            }
        }
        return flag2;
    }
    
     public final int convertSlot(final int rawSlot) {
         // 32
         // 5
         
         // 32 < 5
         
         // slot = 32 - 5 = 27
         // 27 - 4 = 23 + 9 = 31
         
        final int numInTop = this.getTopInventory().getSize();
        if (rawSlot < numInTop) {
            return rawSlot;
        }
        int slot = rawSlot - numInTop;
        if (this.getType() == InventoryType.CRAFTING || this.getType() == InventoryType.CREATIVE) {
            if (slot < 4) {
                return 39 - slot;
            }
            if (slot > 39) {
                return slot;
            }
            slot -= 4;
        }
        if (slot >= 27) {
            slot -= 27;
        }
        else {
            slot += 9;
        }
        return slot;
    }*/
    
    public boolean canShiftClick(@NotNull InventoryType.SlotType slotType, int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Nullable
    public static BukkitInventory getInventory(@NotNull InventoryType type) {
        switch (type) {
            case CHEST:
                return null;
            case DISPENSER:
                return null;
            case DROPPER:
                return null;
            case FURNACE:
                return null;
            case WORKBENCH:
                return null;
            case CRAFTING:
                return null;
            case ENCHANTING:
                return null;
            case BREWING:
                return null;
            case PLAYER:
                return new BukkitPlayerInventory();
            case CREATIVE:
                return null;
            case MERCHANT:
                return null;
            case ENDER_CHEST:
                return null;
            case ANVIL:
                return null;
            case BEACON:
                return null;
            case HOPPER:
                return null;
            case SHULKER_BOX:
                return null;

        }
        return null;
    }

    @Nullable
    public static BukkitItemSlot getEquipmentSlot(@NotNull InventoryType type, @NotNull InventoryType.SlotType slotType, int slot, @NotNull Player player) {
        if (type == InventoryType.PLAYER) {
            switch (slotType) {
                case ARMOR:
                    switch (slot) {
                        case 36:
                            return BukkitItemSlot.FEET;
                        case 37:
                            return BukkitItemSlot.LEGS;
                        case 38:
                            return BukkitItemSlot.CHEST;
                        case 39:
                            return BukkitItemSlot.HEAD;
                    }
                    break;
                case QUICKBAR:
                    switch (slot) {
                        case 40:
                            return BukkitItemSlot.OFF_HAND;
                    }

                    if (slot == player.getInventory().getHeldItemSlot()) {
                        return BukkitItemSlot.MAIN_HAND;
                    }
                    break;
            }
        }
        return null;
    }
}
