package ru.civwars.item;

import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import ru.civwars.CivWars;
import ru.civwars.init.CustomItems;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.EnumInteractionResult;
import ru.civwars.util.InteractionResult;
import ru.lib27.annotation.NotNull;

public class NMSItemStack {

    /* Minecraft ItemStack. */
    private final ItemStack handle;

    /* Bukkit ItemStack. */
    private final org.bukkit.inventory.ItemStack stack;

    /* Custom Item Template. */
    private final CustomItem item;

    private NMSItemStack(@NotNull org.bukkit.inventory.ItemStack stack, @NotNull CustomItem item) {
        this.handle = CraftItemStack.asNMSCopy(stack);
        this.stack = null;
        this.item = item;
    }

    private NMSItemStack(@NotNull org.bukkit.inventory.ItemStack stack) {
        this.handle = CraftItemStack.asNMSCopy(stack);
        this.stack = stack;

        if (this.handle.getTag() != null && this.handle.getTag().hasKeyOfType("mid", 3)) {
            this.item = CustomItems.get(this.handle.getTag().getInt("mid"));
        } else {
            this.item = null;
        }
    }

    private NMSItemStack() {
        this.handle = ItemStack.a;
        this.stack = null;
        this.item = null;
    }

    public boolean isEmpty() {
        return this.handle == null || this.handle.isEmpty();
    }
    
    public boolean isCustom() {
        return this.item != null;
    }

    @NotNull
    public org.bukkit.inventory.ItemStack build() {
        if (this.item != null) {
            this.handle.a("mid", new NBTTagInt(this.item.getId()));

            this.handle.f("item_" + this.item.getName());
        }

        return CraftItemStack.asBukkitCopy(this.handle);
    }

    @NotNull
    public InteractionResult<org.bukkit.inventory.ItemStack> useItemRightClick(@NotNull KPlayer player) {
        if (this.stack != null && this.item != null) {
            return this.item.useItemRightClick(this.stack, player);
        }
        return new InteractionResult<org.bukkit.inventory.ItemStack>(EnumInteractionResult.PASS, this.stack != null ? this.stack : new org.bukkit.inventory.ItemStack(Material.AIR));
    }

    @NotNull
    public InteractionResult<org.bukkit.inventory.ItemStack> useItemRightClickBlock(@NotNull KPlayer player, @NotNull Block block, @NotNull BlockFace facing) {
        if (this.stack != null && this.item != null) {
            return this.item.useItemRightClickBlock(this.stack, player, block, facing);
        }
        return new InteractionResult<org.bukkit.inventory.ItemStack>(EnumInteractionResult.PASS, this.stack != null ? this.stack : new org.bukkit.inventory.ItemStack(Material.AIR));
    }

    @NotNull
    public static NMSItemStack getNMSItemStack(org.bukkit.inventory.ItemStack stack, CustomItem item) {
        if (stack == null || stack.getType() == Material.AIR) {
            return new NMSItemStack();
        }
        return item != null ? new NMSItemStack(stack, item) : new NMSItemStack(stack);
    }

    @NotNull
    public static NMSItemStack getNMSItemStack(org.bukkit.inventory.ItemStack stack) {
        return getNMSItemStack(stack, null);
    }
}
