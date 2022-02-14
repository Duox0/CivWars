package ru.civwars.item.armor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class ItemLeggings extends ItemArmor {

    public ItemLeggings(@NotNull CustomItem.Property property, double defense) {
        super(property, defense);
    }

    @Nullable
    @Override
    public BukkitItemSlot getEquipmentSlot() {
        return BukkitItemSlot.LEGS;
    }

    public static class Serializer extends ItemArmor.Serializer<ItemLeggings> {

        public Serializer() {
            super("leggings", ItemLeggings.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull ItemLeggings item, @NotNull JsonSerializationContext context) {
            super.serialize(object, item, context);
        }

        @NotNull
        @Override
        public ItemLeggings deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double defense) {
            return new ItemLeggings(property, defense);
        }
    }

}
