package ru.civwars.listeners.player;

import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.TaskMaster;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class PlayerSwapHandItemsHandler extends BasicHandler<PlayerSwapHandItemsEvent> {

    public PlayerSwapHandItemsHandler(@NotNull CivWars plugin) {
        super(plugin, PlayerSwapHandItemsEvent.class);
    }

    @Override
    protected void handle(@NotNull PlayerSwapHandItemsEvent event) {
        KPlayer player = this.getPlayer(event.getPlayer());

        ItemStack itemInMainHand = player.getEntity().getInventory().getItemInOffHand();
        ItemStack itemInOffHand = player.getEntity().getInventory().getItemInMainHand();

        if (!ItemUtils.isEmpty(itemInMainHand)) {
            // Проверить возможность носить предмет в левой руке
        }

        TaskMaster.runTask(() -> {
            player.getInventory().onSetItem(BukkitItemSlot.MAIN_HAND);
            player.getInventory().onSetItem(BukkitItemSlot.OFF_HAND);
        });
    }

}
