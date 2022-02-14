package ru.civwars.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import ru.lib27.annotation.NotNull;

public abstract class LootEntityProperty {
    
    public abstract boolean test(@NotNull Random random, @NotNull LivingEntity entity);
    
    public abstract static class Serializer<T extends LootEntityProperty> {

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

        @NotNull
        public abstract JsonElement serialize(@NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonElement element, @NotNull JsonDeserializationContext context);
    }
}