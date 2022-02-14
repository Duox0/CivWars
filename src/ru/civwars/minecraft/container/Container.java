package ru.civwars.minecraft.container;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Slot;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class Container<T extends net.minecraft.server.v1_12_R1.Container> {

    private final T handle;

    public Container(@NotNull T handle) {
        this.handle = handle;
    }

    @NotNull
    public final T getHandle() {
        return this.handle;
    }

    protected List<Integer> a(@NotNull ItemStack stack, int i, int j, boolean flag) {
        List<Integer> triggers = Lists.newArrayList();

        boolean flag2 = false;
        int k = i;
        if (flag) {
            k = j - 1;
        }
        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }
                final Slot slot = this.handle.slots.get(k);
                final ItemStack stackInSlot = slot.getItem();
                if (!stackInSlot.isEmpty() && stackInSlot.getItem() == stack.getItem() && (!stack.usesData() || stack.getData() == stackInSlot.getData()) && ItemStack.equals(stack, stackInSlot)) {
                    final int l = stackInSlot.getCount() + stack.getCount();
                    if (l <= stack.getMaxStackSize()) {
                        triggers.add(k);
                    } else if (stackInSlot.getCount() < stack.getMaxStackSize()) {
                        triggers.add(k);
                    }
                }
                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        if (!stack.isEmpty()) {
            if (flag) {
                k = j - 1;
            } else {
                k = i;
            }
            while (true) {
                if (flag) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }
                final Slot slot = this.handle.slots.get(k);
                final ItemStack itemstack2 = slot.getItem();
                if (itemstack2.isEmpty() && slot.isAllowed(stack)) {
                    triggers.add(k);
                    break;
                }
                if (flag) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        return triggers;
    }
    
    public List<Integer> shiftClick(final EntityHuman entityhuman, final int i) {
        return Lists.newArrayList();
    }
    
    @Nullable
    public static Container getContainer(@NotNull net.minecraft.server.v1_12_R1.Container container) {
        if(container instanceof net.minecraft.server.v1_12_R1.ContainerPlayer) {
            return new ContainerPlayer((net.minecraft.server.v1_12_R1.ContainerPlayer) container);
        }
        return null;
    }
}
