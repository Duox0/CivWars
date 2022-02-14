package ru.civwars.loot.functions;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_12_R1.ItemEnchantedBook;
import net.minecraft.server.v1_12_R1.WeightedRandomEnchant;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.weighted.WeightedEnchantment;
import ru.civwars.util.weighted.WeightedRandomItems;
import ru.civwars.util.math.MathHelper;
import ru.civwars.util.weighted.WeightedRandomItem;
import ru.lib27.annotation.NotNull;

public class EnchantRandomly extends LootFunction {

    private final List<WeightedEnchantment> enchantments;

    public EnchantRandomly(@NotNull LootCondition[] conditions, @NotNull List<WeightedEnchantment> enchantments) {
        super(conditions);
        this.enchantments = Collections.unmodifiableList(enchantments);
    }

    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        Enchantment enchantment;
        int startLevel = 0;
        int maxLevel = 0;

        if (this.enchantments.isEmpty()) {
            ArrayList<Enchantment> enchantments = Lists.newArrayList();
            for (Enchantment enchantment2 : Enchantment.values()) {
                if (stack.getType() == Material.BOOK || enchantment2.canEnchantItem(stack)) {
                    enchantments.add(enchantment2);
                }
            }
            if (enchantments.isEmpty()) {
                Logger.getLogger(EnchantRandomly.class.getSimpleName()).log(Level.WARNING, "Couldn't find a compatible enchantment for {0}", stack);
                return stack;
            }
            enchantment = enchantments.get(context.getRandom().nextInt(enchantments.size()));
            startLevel = enchantment.getStartLevel();
            maxLevel = enchantment.getMaxLevel();
        } else {
            WeightedEnchantment ench = WeightedRandomItems.getRandomItem(context.getRandom(), this.enchantments);
            if (ench == null) {
                return stack;
            }
            enchantment = ench.getEnchantment();
            startLevel = (int) ench.getLevel().getMin();
            maxLevel = (int) ench.getLevel().getMax();
        }
        int nextInt = MathHelper.nextInt(context.getRandom(), startLevel, maxLevel);
        if (stack.getType() == Material.BOOK) {
            stack = new ItemStack(Material.ENCHANTED_BOOK);
            net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
            ItemEnchantedBook.a(nmsStack, new WeightedRandomEnchant(CraftEnchantment.getRaw(enchantment), nextInt));
            stack = CraftItemStack.asBukkitCopy(nmsStack);
        } else {
            stack.addUnsafeEnchantment(enchantment, nextInt);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {

        public Serializer() {
            super("enchant_randomly", EnchantRandomly.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull EnchantRandomly item, @NotNull JsonSerializationContext context) {
            if (!item.enchantments.isEmpty()) {
                JsonArray enchantments = new JsonArray();
                for (WeightedEnchantment enchantment : item.enchantments) {
                    WeightedRandomItem.Serializer<WeightedEnchantment> serializer = WeightedRandomItems.ENCHANTMENT_SERIALIZER;
                    enchantments.add(serializer.serialize(enchantment, context));
                }
                object.add("enchantments", enchantments);
            }
        }

        @NotNull
        @Override
        public EnchantRandomly deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            List<WeightedEnchantment> enchantments = Lists.newArrayList();
            if (object.has("enchantments")) {
                Iterator<JsonElement> iterator = JsonUtils.getJsonArray(object, "enchantments").iterator();
                while (iterator.hasNext()) {
                    WeightedRandomItem.Serializer<WeightedEnchantment> serializer = WeightedRandomItems.ENCHANTMENT_SERIALIZER;
                    enchantments.add(serializer.deserialize(object, context));
                }
            }
            return new EnchantRandomly(conditions, enchantments);
        }
    }
}
