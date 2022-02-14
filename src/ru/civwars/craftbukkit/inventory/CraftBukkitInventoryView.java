package ru.civwars.craftbukkit.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.v1_12_R1.EntityHuman;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.minecraft.container.Container;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CraftBukkitInventoryView {

    private final CraftInventoryView view;
    private final Container container;

    public CraftBukkitInventoryView(@NotNull CraftInventoryView view) {
        this.view = view;
        this.container = Container.getContainer(view.getHandle());
    }

    public List<Integer> shiftClick(@NotNull HumanEntity entity, final int i) {
        if (this.container != null) {
            return this.container.shiftClick(((CraftHumanEntity) entity).getHandle(), i);
        }
        return Lists.newArrayList();
    }
    
    public final void processPlaceItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processPlaceItem");
        ItemStack cursorItem = event.getCursor();
    }

    public final void processPickupItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processPickupItem");
        ItemStack clickedItem = event.getCurrentItem();
    }

    public final void processSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processSwapItems");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
    }

    public final void processDropItem(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processDropItem");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
    }

    public final void processShiftClick(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processShiftClick");
        ItemStack clickedItem = event.getCurrentItem();
        
        this.shiftClick(event.getWhoClicked(), event.getRawSlot()).forEach(slot -> {
            System.out.println("Target slot: " + slot);
        });
    }

    public final void processHotbarSwapItems(@NotNull KPlayer player, @NotNull InventoryClickEvent event) {
        System.out.println("processHotbarSwapItems");
        ItemStack clickedItem = event.getCurrentItem();
        ItemStack hotbar = event.getView().getBottomInventory().getItem(event.getHotbarButton());
    }

}
