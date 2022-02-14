package ru.civwars.bukkit.container;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BukkitContainer {

    protected final CraftInventoryView view2;
    protected final InventoryView view;

    public BukkitContainer(@NotNull InventoryView view) {
        this.view2 = (CraftInventoryView) view;
        this.view = view;
    }

    protected List<Integer> a(@NotNull ItemStack stack, int firstSlot, int lastSlot, boolean flag) {
        List<Integer> closed = Lists.newArrayList();
        boolean flag2 = false;
        int k = firstSlot;
        if (flag) {
            k = lastSlot - 1;
        }

        if (ItemUtils.isStackable(stack)) {
            while (!ItemUtils.isEmpty(stack)) {
                if (flag) {
                    if (k < firstSlot) {
                        break;
                    }
                } else if (k >= lastSlot) {
                    break;
                }

                ItemStack stackInSlot = this.view.getItem(k);
                if (!ItemUtils.isEmpty(stackInSlot) && stackInSlot.isSimilar(stack)) {
                    int l = stackInSlot.getAmount() + stack.getAmount();
                    if (l <= stack.getMaxStackSize()) {
                        closed.add(this.view.convertSlot(k));
                        flag2 = true;
                    } else if (stackInSlot.getAmount() < stack.getMaxStackSize()) {
                        closed.add(this.view.convertSlot(k));
                        flag2 = true;
                    }
                }
                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        
        if (!ItemUtils.isEmpty(stack)) {
            if (flag) {
                k = lastSlot - 1;
            } else {
                k = firstSlot;
            }
            
            while (true) {
                if (flag) {
                    if (k < firstSlot) {
                        break;
                    }
                } else if (k >= lastSlot) {
                    break;
                }
                
                ItemStack stackInSlot = this.view.getItem(k);
                if (ItemUtils.isEmpty(stackInSlot)) {
                    closed.add(k);
                    break;
                }
                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        return closed;
    }
    
    public List<Integer> shiftClick(@NotNull InventoryClickEvent event) {
        return Lists.newArrayList();
    }
    
    @Nullable
    public static BukkitContainer getContainer(@NotNull InventoryType type, @NotNull InventoryView view) {
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
                return new BukkitContainerPlayer(view);
            case ENCHANTING:
                return null;
            case BREWING:
                return null;
            case PLAYER:
                return new BukkitContainerPlayer(view);
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
}
