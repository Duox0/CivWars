package ru.civwars.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class MenuItem {

    private final Material item;
    private final short metadata;

    private final String name;

    private final ClickAction clickAction;

    public MenuItem(@NotNull Material item, short metadata, @NotNull String name, @Nullable ClickAction clickAction) {
        this.item = item;
        this.metadata = metadata;
        this.name = name;

        this.clickAction = clickAction;
    }

    public MenuItem(@NotNull Material item, @NotNull String name, @Nullable ClickAction clickAction) {
        this(item, (short) 0, name, clickAction);
    }

    /**
     *
     * @return материал предмета.
     */
    @NotNull
    public final Material getItem() {
        return this.item;
    }

    /**
     *
     * @return метадата предмета.
     */
    public final short getMetadata() {
        return this.metadata;
    }

    /**
     * @return действие при нажатии, или {@code null}, если не задано.
     */
    @Nullable
    public ClickAction getClickAction() {
        return this.clickAction;
    }
    
    public boolean closeOnAction() {
        return true;
    }

    @NotNull
    public ItemStack createIcon(@NotNull KPlayer player) {
        Material item = this.item != Material.AIR ? this.item : Material.STICK;
        int fixCount = Math.max(1, Math.min(item.getMaxStackSize(), 1));
        int fixData = this.metadata;
        if (item.getMaxDurability() > 0) {
            fixData = Math.max(0, Math.min(item.getMaxDurability(), this.metadata));
        }

        ItemStack stack = new ItemStack(item, fixCount, (short) fixData);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.BLACK + this.name);
        stack.setItemMeta(meta);
        return stack;
    }

}
