package ru.civwars.loot;

import ru.civwars.loot.conditions.LootCondition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import ru.civwars.init.LootTables;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class LootEntryTable extends LootEntry {

    protected final String lootTableId;

    public LootEntryTable(@NotNull String lootTableId, int weight, int quality, @NotNull LootCondition[] conditions) {
        super(weight, quality, conditions);
        this.lootTableId = lootTableId;
    }

    @Override
    public void addLoot(@NotNull Collection<ItemStack> stacks, @NotNull LootContext context) {
        LootTable lootTable = LootTables.get(this.lootTableId);
        Collection<ItemStack> collection = lootTable.generateLootForPools(context);
        stacks.addAll(collection);
    }
    
    public static class Serializer extends LootEntry.Serializer<LootEntryTable> {

        public Serializer() {
            super("loot_table", LootEntryTable.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull LootEntryTable item, @NotNull JsonSerializationContext context) {
            object.addProperty("loot_table_id", item.lootTableId);
        }

        @NotNull
        @Override
        public LootEntryTable deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int weight, int quality, @NotNull LootCondition[] conditions) {
            return new LootEntryTable(JsonUtils.getString(object, "loot_table_id"), weight, quality, conditions);
        }
    }

}
