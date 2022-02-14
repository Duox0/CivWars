package ru.civwars.listeners.entity;

import java.util.Collection;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivWars;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.entity.BasicEntity;
import ru.civwars.init.LootTables;
import ru.civwars.loot.LootContext;
import ru.civwars.loot.LootTable;
import ru.civwars.entity.template.BasicEntityTemplate;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class EntityDeathHandler extends BasicHandler<EntityDeathEvent> {

    public EntityDeathHandler(@NotNull CivWars plugin, @NotNull Class<EntityDeathEvent> eventClass) {
        super(plugin, eventClass);
    }

    @Override
    protected void handle(@NotNull EntityDeathEvent event) {
        CivWorld world = WorldManager.getWorld(event.getEntity().getWorld());
        BasicEntity entity = world.getEntity(event.getEntity());
        
        //clear drops
        event.getDrops().clear();
        //event.setDroppedExp(0);

        BasicEntityTemplate template = entity.getTemplate();
        if (template == null) {
            return;
        }

        LootTable lootTable = LootTables.get("entities/" + template.getName());
        if (lootTable != LootTable.EMPTY_LOOT_TABLE) {
            Collection<ItemStack> drops = lootTable.generateLootForPools(new LootContext(this.random, 0, event.getEntity(), event.getEntity().getKiller()));
            if (!drops.isEmpty()) {
                event.getDrops().addAll(drops);
            }
        }
    }
}
