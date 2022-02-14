package ru.civwars.listeners.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class CraftItemHandler extends BasicHandler<CraftItemEvent> {

    public CraftItemHandler(@NotNull CivWars civcraft) {
        super(civcraft, CraftItemEvent.class);
    }

    @Override
    protected void handle(@NotNull CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        
        KPlayer player = PlayerManager.getPlayer((Player) event.getWhoClicked());
        ItemStack result = event.getInventory().getResult();

        CustomItem item = !ItemUtils.isEmpty(result) ? CustomItems.get().fromItemStack(result) : null;
        if(item != null) {
            event.getInventory().setResult(item.createItemStack(result.getAmount(), player));
        }
    }

}
