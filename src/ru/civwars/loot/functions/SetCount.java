package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.number.RandomInt;
import ru.civwars.loot.LootContext;
import ru.civwars.util.JsonUtils;
import ru.civwars.loot.conditions.LootCondition;
import ru.lib27.annotation.NotNull;

public class SetCount extends LootFunction {

    private final RandomInt count;

    public SetCount(@NotNull LootCondition[] conditions, @NotNull RandomInt count) {
        super(conditions);
        this.count = count;
    }

    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        stack.setAmount(this.count.generateInt(context.getRandom()));
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetCount> {

        public Serializer() {
            super("set_count", SetCount.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull SetCount item, @NotNull JsonSerializationContext context) {
            object.add("count", context.serialize(item.count));
        }

        @NotNull
        @Override
        public SetCount deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            return new SetCount(conditions, JsonUtils.deserialize(object, "count", context, RandomInt.class));
        }
    }
}
