package ru.civwars.loot;

import ru.civwars.loot.conditions.LootCondition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.math.MathHelper;
import ru.lib27.annotation.NotNull;

public abstract class LootEntry {

    protected final int weight;
    protected final int quality;
    protected final LootCondition[] conditions;

    public LootEntry(int weight, int quality, @NotNull LootCondition[] conditions) {
        this.weight = weight;
        this.quality = quality;
        this.conditions = conditions;
    }

    public final int getWeight() {
        return this.weight;
    }

    public final int getQuality() {
        return this.quality;
    }

    public int getEffectiveWeight(double luck) {
        return Math.max(MathHelper.floor(this.weight + this.quality * luck), 0);
    }

    @NotNull
    public final LootCondition[] getConditions() {
        return this.conditions;
    }

    public abstract void addLoot(@NotNull Collection<ItemStack> stacks, @NotNull LootContext context);

    public abstract static class Serializer<T extends LootEntry> {

        private final String name;
        private final Class<T> clazz;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getEntryClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int weight, int quality, @NotNull LootCondition[] conditions);
    }
}
