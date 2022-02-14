package ru.civwars.util.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.ItemUtils;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class RequiredItem {

    /* Необходимое количество предмета. */
    private final int count;

    public RequiredItem(int count) {
        this.count = count;
    }

    /**
     * @return необходимое количество предмета.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * @return необходимый предмет.
     */
    @NotNull
    public abstract Material getItem();

    @NotNull
    public abstract int getMetadata();
    
    /**
     * @return отображаемое имя.
     */
    @NotNull
    public abstract String getDisplayName();

    /**
     * Создает новый объект предмета.
     * @param count - количество предмета.
     * @param player - игрок, создающий предмет.
     * @return предмет.
     */
    @NotNull
    public abstract ItemStack createItemStack(int count, @Nullable KPlayer player);
    
    @NotNull
    public ItemStack createItemStack(int count) {
        return this.createItemStack(count, null);
    }

    public int takeItem(@NotNull Inventory inventory, int count) {
        ItemStack stack = this.createItemStack(1, null);

        int countLeft = count;

        ItemStack[] stacks = inventory.getContents();
        for (int length = stacks.length, i = 0; i < length; i++) {
            ItemStack stackInSlot = stacks[i];
            if (!ItemUtils.isEmpty(stackInSlot)) {
                if (stack.isSimilar(stackInSlot)) {
                    if (countLeft >= stackInSlot.getAmount()) {
                        countLeft -= stackInSlot.getAmount();
                        inventory.setItem(i, null);
                    } else {
                        stackInSlot.setAmount(stackInSlot.getAmount() - countLeft);
                        countLeft = 0;
                    }

                    if (countLeft <= 0) {
                        countLeft = 0;
                        break;
                    }
                }
            }
        }

        return count - countLeft;
    }

    public abstract static class Serializer<T extends RequiredItem> {

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
        public final Class<T> getItemClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int count);
    }
    
}
