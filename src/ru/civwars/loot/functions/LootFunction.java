package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.loot.conditions.LootCondition;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.lib27.annotation.NotNull;

public abstract class LootFunction {
    
    private final LootCondition[] conditions;
    
    protected LootFunction(@NotNull LootCondition[] conditions) {
        this.conditions = conditions;
    }
    
    @NotNull
    public final LootCondition[] getConditions() {
        return this.conditions;
    }
    
    public abstract ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context);
    
    public abstract static class Serializer<T extends LootFunction> {

        private final String name;
        private final Class<T> clazz;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getFunctionClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions);
    }
    
}
