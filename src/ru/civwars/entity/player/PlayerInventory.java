package ru.civwars.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PlayerInventory {

    /* The owner. */
    private final KPlayer owner;

    public final Map<BukkitItemSlot, ItemStack> items = Maps.newHashMap();

    private final List<InventoryListener> listeners;

    public PlayerInventory(@NotNull KPlayer owner) {
        this.owner = owner;

        this.listeners = Lists.newArrayList();

        this.listeners.add(new StatsListener());
    }

    public void restore() {
        Player player = this.owner.getEntity();
        if (player != null) {
            for (BukkitItemSlot slot : BukkitItemSlot.values()) {
                this.onSetItem(slot);
            }
        }
    }

    /**
     * Gets the owner.
     *
     * @return The PlayerInstance
     */
    @NotNull
    public KPlayer getOwner() {
        return this.owner;
    }

    public void onSetItem(@NotNull BukkitItemSlot slot) {
        System.out.println("onSetItem");
        Player player = this.owner.getEntity();
        if (player != null) {
            ItemStack stack = slot.getItemStackFromSlot(player, slot);
            ItemStack oldStack = this.items.remove(slot);

            if(stack != null && oldStack != null && stack.isSimilar(oldStack)) {
                return;
            }
            
            if (!ItemUtils.isEmpty(oldStack)) {
                for (InventoryListener listener : this.listeners) {
                    listener.notifyUnequiped(slot, oldStack);
                }
            }

            if (!ItemUtils.isEmpty(stack)) {
                ItemStack clone = stack.clone();
                this.items.put(slot, clone);
                for (InventoryListener listener : this.listeners) {
                    listener.notifyEquiped(slot, clone);
                }
            }
        }
    }

    interface InventoryListener {

        public void notifyEquiped(@NotNull BukkitItemSlot slot, @Nullable ItemStack stack);

        public void notifyUnequiped(@NotNull BukkitItemSlot slot, @Nullable ItemStack stack);
    }

    final class StatsListener implements InventoryListener {

        Map<BukkitItemSlot, Boolean> closed = Maps.newHashMap();

        @Override
        public void notifyEquiped(@NotNull BukkitItemSlot slot, @NotNull ItemStack stack) {
            System.out.println("notifyEquiped");
            if (this.closed.getOrDefault(slot, false)) {
                return;
            }
            
            CustomItem item = CustomItems.get().fromItemStack(stack);
            if (item == null || item.getEquipmentSlot() != slot) {
                return;
            }

            System.out.println("notifyEquiped::add");
            this.closed.put(slot, true);
            getOwner().getStat().addStatFunctions(item.getStatFunctions(stack));
        }

        @Override
        public void notifyUnequiped(@NotNull BukkitItemSlot slot, @NotNull ItemStack stack) {
            System.out.println("notifyUnequiped");
            if (!this.closed.getOrDefault(slot, false)) {
                return;
            }
            System.out.println("notifyUnequiped:remove");
            getOwner().getStat().removeStatsSource(stack);
            this.closed.put(slot, false);
        }
    }
}
