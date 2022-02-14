package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.number.RandomDouble;
import ru.civwars.loot.LootContext;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.math.MathHelper;
import ru.civwars.loot.conditions.LootCondition;
import ru.lib27.annotation.NotNull;

public class SetDurability extends LootFunction {

    private final RandomDouble durability;

    public SetDurability(@NotNull LootCondition[] conditions, @NotNull RandomDouble durability) {
        super(conditions);
        this.durability = durability;
    }

    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (stack.getType().getMaxDurability() > 0) {
            double rate = 1.0f - this.durability.generateDouble(context.getRandom());
            stack.setDurability((short) MathHelper.floor(rate * stack.getType().getMaxDurability()));
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetDurability> {

        public Serializer() {
            super("set_durability", SetDurability.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull SetDurability item, @NotNull JsonSerializationContext context) {
            object.add("durability", context.serialize(item.durability));
        }

        @NotNull
        @Override
        public SetDurability deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            return new SetDurability(conditions, JsonUtils.deserialize(object, "durability", context, RandomDouble.class));
        }
    }
}
