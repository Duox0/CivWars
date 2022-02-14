package ru.civwars.item.armor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class ItemHelmet extends ItemArmor {

    public ItemHelmet(@NotNull CustomItem.Property property, double defense) {
        super(property, defense);
    }

    @Nullable
    @Override
    public BukkitItemSlot getEquipmentSlot() {
        return BukkitItemSlot.HEAD;
    }

    public static class Serializer extends ItemArmor.Serializer<ItemHelmet> {

        public Serializer() {
            super("helmet", ItemHelmet.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull ItemHelmet item, @NotNull JsonSerializationContext context) {
            super.serialize(object, item, context);
        }

        @NotNull
        @Override
        public ItemHelmet deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double defense) {
            return new ItemHelmet(property, defense);
        }
    }

}
