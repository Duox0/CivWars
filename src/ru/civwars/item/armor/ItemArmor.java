package ru.civwars.item.armor;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.item.CustomItem;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public abstract class ItemArmor extends CustomItem {

    private final double defense;
    
    public ItemArmor(@NotNull CustomItem.Property property, double defense) {
        super(property);
        this.defense = defense;
    }
    
    /**
     * Получает броню от предмета.
     * @return 
     */
    public double getDefense() {
        return this.defense;
    }

    public abstract static class Serializer<T extends ItemArmor> extends CustomItem.Serializer<T> {

        public Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            super(name, clazz);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context) {
            object.addProperty("defense", item.getDefense());
        }

        @NotNull
        @Override
        public final T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property) {
            double defense = JsonUtils.getDouble(object, "defense");
            return this.deserialize(object, context, property, defense);
        }
        
        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double defense);
    }
}
