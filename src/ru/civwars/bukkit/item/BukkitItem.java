package ru.civwars.bukkit.item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BukkitItem {

    private final Material item;

    public BukkitItem(@NotNull Material item) {
        this.item = item;
    }

    @NotNull
    public final Material getItem() {
        return this.item;
    }
    
    @Nullable
    public BukkitItemSlot getEquipmentSlot(@NotNull ItemStack stack) {
        return null;
    }

    public void onUseItemRightClick(@NotNull ItemStack stack, @NotNull KPlayer player, @NotNull BukkitItemSlot hand) {
    }

    public void onUseItemRightClickBlock(@NotNull ItemStack stack, @NotNull KPlayer player, @NotNull Block block, @NotNull BlockFace facing) {
    }
}
