package ru.civwars.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.loot.LootContext;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class RandomChance extends LootCondition {

    private final double chance;

    public RandomChance(double chance) {
        this.chance = chance;
    }

    @Override
    public boolean testCondition(@NotNull LootContext context) {
        return context.getRandom().nextDouble() < this.chance;
    }

    public static class Serializer extends LootCondition.Serializer<RandomChance> {

        public Serializer() {
            super("random_chance", RandomChance.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull RandomChance item, @NotNull JsonSerializationContext context) {
            object.addProperty("chance", item.chance);
        }

        @NotNull
        @Override
        public RandomChance deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new RandomChance(JsonUtils.getDouble(object, "chance"));
        }
    }

}
