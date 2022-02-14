package ru.civwars.item.armor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class ItemShield extends ItemArmor {

    public ItemShield(@NotNull CustomItem.Property property, double defense) {
        super(property, defense);
    }

    @Nullable
    @Override
    public BukkitItemSlot getEquipmentSlot() {
        return BukkitItemSlot.OFF_HAND;
    }

    public static class Serializer extends ItemArmor.Serializer<ItemShield> {

        public Serializer() {
            super("shield", ItemShield.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull ItemShield item, @NotNull JsonSerializationContext context) {
            super.serialize(object, item, context);
        }

        @NotNull
        @Override
        public ItemShield deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double defense) {
            return new ItemShield(property, defense);
        }
    }

}
