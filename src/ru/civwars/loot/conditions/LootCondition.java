package ru.civwars.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.loot.LootContext;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class LootCondition {

    public abstract boolean testCondition(@NotNull LootContext context);

    public static boolean testAllConditions(@Nullable LootCondition[] conditions, @NotNull LootContext context) {
        if (conditions == null) {
            return true;
        }
        for (LootCondition condition : conditions) {
            if (!condition.testCondition(context)) {
                return false;
            }
        }
        return true;
    }
    
    public abstract static class Serializer<T extends LootCondition> {

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
        public final Class<T> getConditionClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context);
    }
}
