package ru.civwars.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class EntityOnFire extends LootEntityProperty {

    private final boolean onFire;

    public EntityOnFire(boolean onFire) {
        this.onFire = onFire;
    }

    @Override
    public boolean test(@NotNull Random random, @NotNull LivingEntity entity) {
        return (this.onFire == (entity.getFireTicks() > 0));
    }

    public static class Serializer extends LootEntityProperty.Serializer<EntityOnFire> {

        public Serializer() {
            super("set_damage", EntityOnFire.class);
        }

        @NotNull
        @Override
        public JsonElement serialize(@NotNull EntityOnFire item, @NotNull JsonSerializationContext context) {
            return new JsonPrimitive(item.onFire);
        }

        @NotNull
        @Override
        public EntityOnFire deserialize(@NotNull JsonElement element, @NotNull JsonDeserializationContext context) {
            return new EntityOnFire(JsonUtils.getBoolean(element, "on_fire"));
        }
    }
}
