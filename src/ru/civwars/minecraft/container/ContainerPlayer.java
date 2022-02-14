package ru.civwars.minecraft.container;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.Slot;
import ru.lib27.annotation.NotNull;

public class ContainerPlayer extends Container<net.minecraft.server.v1_12_R1.ContainerPlayer> {

    public ContainerPlayer(@NotNull net.minecraft.server.v1_12_R1.ContainerPlayer handle) {
        super(handle);
    }

    @Override
    public List<Integer> shiftClick(final EntityHuman entityhuman, final int i) {
        List<Integer> slots = Lists.newArrayList();

        Slot slot = this.getHandle().slots.get(i);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            ItemStack copyItemStack = stackInSlot.cloneItemStack();
            EnumItemSlot equipmentSlot = EntityInsentient.d(copyItemStack);
            if (i == 0) {
                if ((slots = this.a(stackInSlot, 9, 45, true)).isEmpty()) {
                    return slots;
                }
            } else if (i >= 1 && i < 5) {
                if ((slots = this.a(stackInSlot, 9, 45, false)).isEmpty()) {
                    return slots;
                }
            } else if (i >= 5 && i < 9) {
                if ((slots = this.a(stackInSlot, 9, 45, false)).isEmpty()) {
                    return slots;
                }
            } else if (equipmentSlot.a() == EnumItemSlot.Function.ARMOR && !this.getHandle().slots.get(8 - equipmentSlot.b()).hasItem()) {
                final int j = 8 - equipmentSlot.b();
                if ((slots = this.a(stackInSlot, j, j + 1, false)).isEmpty()) {
                    return slots;
                }
            } else if (equipmentSlot == EnumItemSlot.OFFHAND && !this.getHandle().slots.get(45).hasItem()) {
                if ((slots = this.a(stackInSlot, 45, 46, false)).isEmpty()) {
                    return slots;
                }
            } else if (i >= 9 && i < 36) {
                if ((slots = this.a(stackInSlot, 36, 45, false)).isEmpty()) {
                    return slots;
                }
            } else if (i >= 36 && i < 45) {
                if ((slots = this.a(stackInSlot, 9, 36, false)).isEmpty()) {
                    return slots;
                }
            } else if ((slots = this.a(stackInSlot, 9, 45, false)).isEmpty()) {
                return slots;
            }
        }

        return slots;
    }
}
