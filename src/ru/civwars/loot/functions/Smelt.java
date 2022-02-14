package ru.civwars.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_12_R1.RecipesFurnace;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import ru.civwars.loot.LootContext;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class Smelt extends LootFunction {

    public Smelt(@NotNull LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack apply(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (ItemUtils.isEmpty(stack)) {
            return stack;
        }
        
        net.minecraft.server.v1_12_R1.ItemStack result = RecipesFurnace.getInstance().getResult(CraftItemStack.asNMSCopy(stack));
        if (result.isEmpty()) {
            Logger.getLogger(Smelt.class.getSimpleName()).log(Level.WARNING, "Couldn''t smelt {0} because there is no smelting recipe", stack);
            return stack;
        }
        net.minecraft.server.v1_12_R1.ItemStack cloneItemStack = result.cloneItemStack();
        cloneItemStack.setCount(stack.getAmount());
        return CraftItemStack.asBukkitCopy(cloneItemStack);
    }
    
    public static class Serializer extends LootFunction.Serializer<Smelt> {

        public Serializer() {
            super("furnace_smelt", Smelt.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull Smelt item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public Smelt deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull LootCondition[] conditions) {
            return new Smelt(conditions);
        }
    }
}