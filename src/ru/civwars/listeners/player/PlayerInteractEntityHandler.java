package ru.civwars.listeners.player;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.BasicEntity;
import ru.civwars.entity.NPCEntity;
import ru.civwars.entity.MobEntity;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class PlayerInteractEntityHandler extends BasicHandler<PlayerInteractEntityEvent> {

    public PlayerInteractEntityHandler(@NotNull CivWars plugin) {
        super(plugin, PlayerInteractEntityEvent.class);
    }

    @Override
    protected void handle(@NotNull PlayerInteractEntityEvent event) {
        CivWorld world = WorldManager.getWorld(event.getPlayer().getWorld());
        
        KPlayer player = this.getPlayer(event.getPlayer());
        BasicEntity entity = world.getEntity(event.getRightClicked());
        if(entity == null) {
            entity = new NPCEntity(event.getRightClicked());
        }
        
        if(entity instanceof MobEntity) {
            player.sendMessage("Hellow, i'am custom mob:)");
        }
    }

}
