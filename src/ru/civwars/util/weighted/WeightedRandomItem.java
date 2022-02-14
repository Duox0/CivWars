package ru.civwars.util.weighted;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.lib27.annotation.NotNull;

public class WeightedRandomItem {

    protected final int weight;

    public WeightedRandomItem(int weight) {
        this.weight = weight;
    }

    public final int getWeight() {
        return this.weight;
    }

    public abstract static class Serializer<T extends WeightedRandomItem> {

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
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context);
    }

}
