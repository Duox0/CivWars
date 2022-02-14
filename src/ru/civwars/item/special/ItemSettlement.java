package ru.civwars.item.special;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;

public class ItemSettlement extends CustomItem {

    public ItemSettlement(@NotNull CustomItem.Property property) {
        super(property);
    }

    public static class Serializer extends CustomItem.Serializer<ItemSettlement> {

        public Serializer() {
            super("settlement", ItemSettlement.class);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull ItemSettlement item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public ItemSettlement deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property) {
            return new ItemSettlement(property);
        }
    }

}
