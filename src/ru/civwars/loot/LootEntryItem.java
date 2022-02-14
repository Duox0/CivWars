package ru.civwars.loot;

import ru.civwars.loot.functions.LootFunction;
import ru.civwars.loot.conditions.LootCondition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class LootEntryItem extends LootEntry {

    protected final Material item;
    protected final LootFunction[] functions;

    public LootEntryItem(@NotNull Material item, int weight, int quality, @NotNull LootCondition[] conditions, @NotNull LootFunction[] functions) {
        super(weight, quality, conditions);
        this.item = item;
        this.functions = functions;
    }

    @Override
    public void addLoot(@NotNull Collection<ItemStack> stacks, @NotNull LootContext context) {
        ItemStack stack = new ItemStack(this.item);
        for (LootFunction function : this.functions) {
            if (LootCondition.testAllConditions(function.getConditions(), context)) {
                stack = function.apply(stack, context);
            }
        }
        if (!ItemUtils.isEmpty(stack)) {
            if (stack.getAmount() < this.item.getMaxStackSize()) {
                stacks.add(stack);
            } else {
                int i = stack.getAmount();
                while (i > 0) {
                    ItemStack stack2 = stack.clone();
                    stack2.setAmount(Math.min(stack.getMaxStackSize(), i));
                    i -= stack2.getAmount();
                    stacks.add(stack2);
                }
            }
        }
    }

    public static class Serializer extends LootEntry.Serializer<LootEntryItem> {

        public Serializer() {
            super("minecraft_item", LootEntryItem.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull LootEntryItem item, @NotNull JsonSerializationContext context) {
            object.add("item_id", context.serialize(item.item, Material.class));
            if (item.functions != null && item.functions.length > 0) {
                object.add("functions", context.serialize(item.functions));
            }
        }

        @NotNull
        @Override
        public LootEntryItem deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int weight, int quality, @NotNull LootCondition[] conditions) {
            return new LootEntryItem(JsonUtils.deserialize(object, "item_id", context, Material.class), weight, quality,
                    conditions,
                    JsonUtils.deserializeOrDefault(object, "functions", new LootFunction[0], context, LootFunction[].class));
        }
    }
}
