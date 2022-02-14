package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.civwars.util.JsonUtils;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.number.RandomInt;
import ru.lib27.annotation.NotNull;

public class SetMetadata extends LootFunction {

    private final RandomInt meta;

    public SetMetadata(@NotNull LootCondition[] conditions, @NotNull RandomInt meta) {
        super(conditions);
        this.meta = meta;
    }
    
    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (stack.getType().getMaxDurability() > 0) {
            Logger.getLogger(SetMetadata.class.getSimpleName()).warning("Couldn't set data of loot item " + stack);
        } else {
            stack.setDurability((short) this.meta.generateInt(context.getRandom()));
        }
        return stack;
    }
    
    public static class Serializer extends LootFunction.Serializer<SetMetadata> {

        public Serializer() {
            super("set_data", SetMetadata.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull SetMetadata item, @NotNull JsonSerializationContext context) {
            object.add("data", context.serialize(item.meta));
        }

        @NotNull
        @Override
        public SetMetadata deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            return new SetMetadata(conditions, JsonUtils.deserialize(object, "data", context, RandomInt.class));
        }
    }
}
