package ru.civwars.listeners.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.bukkit.inventory.BukkitInventory;
import ru.civwars.craftbukkit.inventory.CraftBukkitInventoryView;
import ru.civwars.menu.ClickAction;
import ru.civwars.menu.Menu;
import ru.civwars.menu.MenuItem;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class InventoryClickHandler extends BasicHandler<InventoryClickEvent> {

    public InventoryClickHandler(@NotNull CivWars plugin) {
        super(plugin, InventoryClickEvent.class);
    }

    @Override
    protected void handle(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (1 == 2) {
            System.out.println("==========[ InventoryClickEvent ]==========");
            Map<String, Object> objects = Maps.newHashMap();
            objects.put("Action", event.getAction());
            objects.put("Click", event.getClick());
            objects.put("ClickedInventory", event.getClickedInventory());
            objects.put("CurrentItem", event.getCurrentItem());
            objects.put("Cursor", event.getCursor());
            objects.put("HotbarButton", event.getHotbarButton());
            objects.put("Inventory", event.getInventory());
            objects.put("RawSlot", event.getRawSlot());
            objects.put("Slot", event.getSlot());
            objects.put("SlotType", event.getSlotType());
            for (Map.Entry entry : objects.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        }

        KPlayer player = this.getPlayer((Player) event.getWhoClicked());

        Inventory clickedInventory = event.getClickedInventory();
        int slot = event.getSlot();
        if (clickedInventory.getHolder() instanceof Menu) {
            Menu menu = (Menu) clickedInventory.getHolder();
            if (player.getCurrentMenu() != menu) {
                player.getEntity().closeInventory();
                event.setCancelled(true);
                return;
            }

            MenuItem menuItem = menu.getItem(player, slot);
            if (menuItem != null) {
                if (menuItem.closeOnAction()) {
                    player.getEntity().closeInventory();
                }

                ClickAction clickAction = menuItem.getClickAction();
                if (clickAction != null) {
                    clickAction.onClick(player, event.getClick(), slot);
                    event.setCancelled(true);
                }
            }
            return;
        }

        this.process(player, event);
    }

    private void process(@NotNull KPlayer player, InventoryClickEvent event) {
        CraftBukkitInventoryView view = new CraftBukkitInventoryView((CraftInventoryView) event.getView());

        InventoryType type = event.getClickedInventory().getType();
        BukkitInventory inv = BukkitInventory.getInventory(type);
        if (inv == null) {
            return;
        }

        switch (event.getAction()) {
            // ???????? ?????????????? ?? ??????????
            case PICKUP_ALL: //??????
            case PICKUP_SOME:
            case PICKUP_HALF: // ?????? (????????????????, ???????? ???????????? 1)
            case PICKUP_ONE:
                view.processPickupItem(player, event);
                inv.processTryPickupItem(player, event);
                break;
            // ?????????????? ?????????????? ?? ??????????????
            case PLACE_ALL: // ??????
            case PLACE_SOME:
            case PLACE_ONE: // ??????
                view.processPlaceItem(player, event);
                inv.processTryPlaceItem(player, event);
                break;
            // ?????????????? ???????????????? (???????? <---> ????????????)
            case SWAP_WITH_CURSOR:
                view.processSwapItems(player, event);
                inv.processTrySwapItems(player, event);
                break;
            // ?????????????? ?????????????? ???? ?????????????? (?????? ??????????????????)
            case DROP_ALL_CURSOR: // ??????
            case DROP_ONE_CURSOR: // ??????
                break;
            // ?????????????? ?????????????? ???? ?????????? ???????????????? Q
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT: // Ctrl + Q
                view.processDropItem(player, event);
                inv.processTryDropItem(player, event);
                break;
            // ?????????????????????? ???????????????? ?? ?????????????? ?????????????? Shift
            case MOVE_TO_OTHER_INVENTORY:
                view.processShiftClick(player, event);
                inv.processTryShiftClick(player, event);
                break;
            // ?????????????? ???????????????? (hotbar <---> ????????)
            case HOTBAR_MOVE_AND_READD: // ?????? ???????????????? NotNull
            case HOTBAR_SWAP: // ???????? ???? ?????????????????? NULL
                view.processHotbarSwapItems(player, event);
                inv.processTryHotbarSwapItems(player, event);
                break;
            case CLONE_STACK:
                // ???????????????????????? ???????????????? ??????????????????
                break;
            case COLLECT_TO_CURSOR:
                // ???? ??????????
                break;
        }
    }

}
