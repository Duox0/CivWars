package ru.civwars.util.weighted;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.enchantments.Enchantment;
import ru.civwars.util.number.RandomDouble;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class WeightedEnchantment extends WeightedRandomItem {

    private final Enchantment enchantment;
    private final RandomDouble level;

    public WeightedEnchantment(@NotNull Enchantment enchantment, @NotNull RandomDouble level, int weight) {
        super(weight);
        this.enchantment = enchantment;
        this.level = level;
    }

    @NotNull
    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    @NotNull
    public RandomDouble getLevel() {
        return this.level;
    }

    public static class Serializer extends WeightedRandomItem.Serializer<WeightedEnchantment> {

        public Serializer() {
            super("enchantment", WeightedEnchantment.class);
        }

        @NotNull
        @Override
        public JsonElement serialize(@NotNull WeightedEnchantment item, @NotNull JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("enchantment", item.enchantment.getName().toLowerCase());
            object.add("level", context.serialize(item.level));
            object.addProperty("weight", item.getWeight());
            return object;
        }

        @NotNull
        @Override
        public WeightedEnchantment deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            Enchantment enchantment = JsonUtils.getEnchantment(object, "enchantment");
            RandomDouble level = JsonUtils.deserializeOrDefault(object, "enchantment", new RandomDouble(1.0), context, RandomDouble.class);
            int weight = JsonUtils.getIntOrDefault(object, "weight", 1);
            return new WeightedEnchantment(enchantment, level, weight);
        }
    }
}
