package ru.civwars.loot;

import ru.civwars.loot.conditions.LootCondition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import ru.lib27.annotation.NotNull;

public class LootEntryEmpty extends LootEntry {

    public LootEntryEmpty(int weight, int quality, @NotNull LootCondition[] conditions) {
        super(weight, quality, conditions);
    }

    @Override
    public void addLoot(@NotNull Collection<ItemStack> stacks, @NotNull LootContext context) {
        
    }

    public static class Serializer extends LootEntry.Serializer<LootEntryEmpty> {

        public Serializer() {
            super("empty", LootEntryEmpty.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull LootEntryEmpty item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public LootEntryEmpty deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int weight, int quality, @NotNull LootCondition[] conditions) {
            return new LootEntryEmpty(weight, quality, conditions);
        }
    }
}
