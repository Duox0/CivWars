package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class Looting extends LootFunction {

    private final Enchantment enchantment;
    private final int limit;

    public Looting(@NotNull LootCondition[] conditions, @NotNull Enchantment enchantment, int limit) {
        super(conditions);
        this.enchantment = enchantment;
        this.limit = limit;
    }

    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        LivingEntity entity = context.getKiller();
        if (entity != null) {
            int level = 0;
            ItemStack inHand = entity.getEquipment().getItemInMainHand();
            if (!ItemUtils.isEmpty(inHand)) {
                level = inHand.getEnchantmentLevel(this.enchantment);
            }

            if (level == 0) {
                return stack;
            }

            stack.setAmount(stack.getAmount() + context.getRandom().nextInt(level + 1));

            int limit = Math.min(this.limit, stack.getType().getMaxStackSize());

            if (limit > 0 && stack.getAmount() > limit) {
                stack.setAmount(limit);
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<Looting> {

        public Serializer() {
            super("looting", Looting.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull Looting item, @NotNull JsonSerializationContext context) {
            object.addProperty("enchantment", item.enchantment.getName().toLowerCase());
            if (item.limit > 0) {
                object.addProperty("limit", item.limit);
            }
        }

        @NotNull
        @Override
        public Looting deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            return new Looting(conditions, JsonUtils.getEnchantment(object, "enchantment"), JsonUtils.getIntOrDefault(object, "limit", 0));
        }
    }
}
