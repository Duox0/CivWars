package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import ru.civwars.CivWars;
import ru.civwars.entity.BasicEntity;
import ru.civwars.listeners.entity.*;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class EntityListener extends BasicListener {

    private static Listener instance;

    public static void init(@NotNull CivWars plugin) {
        if (instance == null) {
            instance = new EntityListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }

    public EntityListener(@NotNull CivWars plugin) {
        super(plugin);

        this.registerHandler(new EntityChangeBlockHandler(plugin, EntityChangeBlockEvent.class));
        this.registerHandler(new EntityDeathHandler(plugin, EntityDeathEvent.class));
        this.registerHandler(new SlimeSplitHandler(plugin, SlimeSplitEvent.class));
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        this.handle0(event);
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        CivWorld world = WorldManager.getWorld(event.getEntity().getWorld());

        BasicEntity entity = world.getEntity(event.getEntity());
        BasicEntity attacker = world.getEntity(event.getDamager());

        StringBuilder builder = new StringBuilder();

        //Attacker=?,Target=?,Damage=?,Defense=?
        builder.append("Attacker=").append(attacker.getName()).append(",");
        builder.append("Target=").append(entity.getName()).append(",");
        builder.append("Damage=").append(attacker.getStat().getAttackDamage(entity)).append(",");
        builder.append("Defense=").append(entity.getStat().getDefense(attacker));
        System.out.println(builder.toString());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        this.handle0(event);
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        this.handle0(event);
    }

}
