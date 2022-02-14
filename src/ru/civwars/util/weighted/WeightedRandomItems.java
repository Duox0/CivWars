package ru.civwars.util.weighted;

import java.util.List;
import java.util.Random;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class WeightedRandomItems {

    public static final WeightedEnchantment.Serializer ENCHANTMENT_SERIALIZER = new WeightedEnchantment.Serializer();

    public static int getTotalWeight(@NotNull List<? extends WeightedRandomItem> items) {
        int n = 0;
        for (WeightedRandomItem item : items) {
            n += item.weight;
        }
        return n;
    }

    @Nullable
    public static <T extends WeightedRandomItem> T getRandomItem(@NotNull Random random, @NotNull List<T> items, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException();
        }
        final int i = random.nextInt(totalWeight);
        return getRandomItem(items, i);
    }

    @Nullable
    public static <T extends WeightedRandomItem> T getRandomItem(@NotNull List<T> items, int totalWeight) {
        for (T item : items) {
            totalWeight -= item.weight;
            if (totalWeight < 0) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    public static <T extends WeightedRandomItem> T getRandomItem(@NotNull Random random, final List<T> items) {
        return getRandomItem(random, items, getTotalWeight(items));
    }

}
