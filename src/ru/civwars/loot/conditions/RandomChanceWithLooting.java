package ru.civwars.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class RandomChanceWithLooting extends LootCondition {

    private final double chance;
    private final double lootingMultiplier;

    public RandomChanceWithLooting(double chance, double lootingMultiplier) {
        this.chance = chance;
        this.lootingMultiplier = lootingMultiplier;
    }

    @Override
    public boolean testCondition(@NotNull LootContext context) {
        int level = 0;
        if (context.getKiller() instanceof LivingEntity) {
            ItemStack stack = context.getKiller().getEquipment().getItemInMainHand();
            if (!ItemUtils.isEmpty(stack)) {
                level = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            }
        }
        return context.getRandom().nextFloat() < this.chance + level * this.lootingMultiplier;
    }

    public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting> {

        public Serializer() {
            super("random_chance_with_looting", RandomChanceWithLooting.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull RandomChanceWithLooting item, @NotNull JsonSerializationContext context) {
            object.addProperty("chance", item.chance);
            object.addProperty("looting_multiplier", item.lootingMultiplier);
        }

        @NotNull
        @Override
        public RandomChanceWithLooting deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new RandomChanceWithLooting(JsonUtils.getDouble(object, "chance"), JsonUtils.getDouble(object, "looting_multiplier"));
        }
    }

}
