package ru.civwars.item.weapon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.civwars.item.CustomItem;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class ItemWeapon extends CustomItem {

    private final double attackDamage;

    public ItemWeapon(@NotNull CustomItem.Property property, double attackDamage) {
        super(property);
        this.attackDamage = attackDamage;
    }

    /**
     * Получает урон от предмета.
     *
     * @return
     */
    public double getAttackDamage() {
        return this.attackDamage;
    }

    @Nullable
    @Override
    public BukkitItemSlot getEquipmentSlot() {
        return BukkitItemSlot.MAIN_HAND;
    }

    public abstract static class Serializer<T extends ItemWeapon> extends CustomItem.Serializer<T> {

        public Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            super(name, clazz);
        }

        @NotNull
        @Override
        public void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context) {
            object.addProperty("attack_damage", item.getAttackDamage());
        }

        @NotNull
        @Override
        public final T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property) {
            double attackDamage = JsonUtils.getDouble(object, "attack_damage");
            return this.deserialize(object, context, property, attackDamage);
        }

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property, double attackDamage);
    }
}
