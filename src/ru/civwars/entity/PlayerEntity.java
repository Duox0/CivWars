package ru.civwars.entity;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.civwars.i18n.I18n;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.entity.template.PlayerTemplate;
import ru.civwars.stat.BasicStat;
import ru.civwars.stat.PlayerStat;
import ru.lib27.annotation.NotNull;

public abstract class PlayerEntity extends BasicEntity<Player> {

    private final PlayerTemplate template;

    public PlayerEntity(@NotNull Player entity, @NotNull PlayerTemplate template) {
        super(entity);
        this.template = template;
    }

    @NotNull
    @Override
    protected BasicStat createStat() {
        return new PlayerStat(this);
    }

    @NotNull
    @Override
    public final PlayerTemplate getTemplate() {
        return this.template;
    }

    @Override
    public void despawn() {
    }
    
    public boolean isOnline() {
        return this.getEntity().isOnline();
    }
    
    public void setHeldItem(@NotNull BukkitItemSlot slot, ItemStack stack) {
        if (slot == BukkitItemSlot.MAIN_HAND) {
            this.getEntity().getInventory().setItemInMainHand(stack);
        } else if (slot == BukkitItemSlot.OFF_HAND) {
            this.getEntity().getInventory().setItemInOffHand(stack);
        }
    }
    
    public ItemStack getHeldItem(@NotNull BukkitItemSlot slot) {
        if (slot == BukkitItemSlot.MAIN_HAND) {
            return this.getEntity().getInventory().getItemInMainHand();
        } else if (slot == BukkitItemSlot.OFF_HAND) {
            return this.getEntity().getInventory().getItemInOffHand();
        }
        return null;
    }
    
    public void sendMessage(@NotNull String key, Object... objects) {
        this.getEntity().sendMessage(I18n.tl(key, objects));
    }
    
    // ========================= SPIGOT ========================
    public void sendMessage(@NotNull BaseComponent text) {
        this.getEntity().spigot().sendMessage(text);
    }
}
