package ru.civwars.listeners.player;

import org.bukkit.event.player.PlayerExpChangeEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.lib27.annotation.NotNull;

public class PlayerExpChangeHandler extends BasicHandler<PlayerExpChangeEvent> {

    public PlayerExpChangeHandler(@NotNull CivWars civcraft) {
        super(civcraft, PlayerExpChangeEvent.class);
    }

    @Override
    protected void handle(@NotNull PlayerExpChangeEvent event) {
        KPlayer player = PlayerManager.getPlayer(event.getPlayer());
        
        player.sendMessage("pickupMoney", event.getAmount());
        player.changeGold(event.getAmount());
        event.setAmount(0);
    }
}
