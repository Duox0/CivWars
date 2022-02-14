package ru.civwars.item.weapon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;

public class ItemSword extends ItemWeapon {

    public ItemSword(@NotNull CustomItem.Property property, double attackDamage) {
        super(property, attackDamage);
    }

    public static class Serializer extends ItemWeapon.Serializer<ItemSword> {

        public Serializer() {
            super("sword", ItemSword.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull ItemSword item, @NotNull JsonSerializationContext context) {
            super.serialize(object, item, context);
        }

        @NotNull
        @Override
        public ItemSword deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double attackDamage) {
            return new ItemSword(property, attackDamage);
        }
    }

}
