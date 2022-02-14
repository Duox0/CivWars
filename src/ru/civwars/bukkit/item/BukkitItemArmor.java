package ru.civwars.bukkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.TaskMaster;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BukkitItemArmor extends BukkitItem {

    private final BukkitItemSlot slot;
    
    public BukkitItemArmor(@NotNull Material item, @NotNull BukkitItemSlot slot) {
        super(item);
        this.slot = slot;
    }
    
    @Nullable
    @Override
    public BukkitItemSlot getEquipmentSlot(@NotNull ItemStack stack) {
        return this.slot;
    }

    @Override
    public void onUseItemRightClick(@NotNull ItemStack stack, @NotNull KPlayer player, @NotNull BukkitItemSlot hand) {
        BukkitItemSlot slotForItemStack = this.getEquipmentSlot(stack);
        final ItemStack stackInSlot = slotForItemStack.getItemStackFromSlot(player.getEntity(), slotForItemStack);
        if (ItemUtils.isEmpty(stackInSlot)) {
            TaskMaster.runTask(() -> {
                player.getInventory().onSetItem(hand == BukkitItemSlot.OFF_HAND ? BukkitItemSlot.OFF_HAND : BukkitItemSlot.MAIN_HAND);
                player.getInventory().onSetItem(slotForItemStack);
            });
        }
    }
}
