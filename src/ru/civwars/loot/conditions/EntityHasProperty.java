package ru.civwars.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.LivingEntity;
import ru.civwars.loot.LootContext;
import ru.civwars.loot.properties.LootEntityProperties;
import ru.civwars.loot.properties.LootEntityProperty;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class EntityHasProperty extends LootCondition {

    private final LootContext.EntityTarget target;
    private final LootEntityProperty[] properties;

    public EntityHasProperty(@NotNull LootContext.EntityTarget target, @NotNull LootEntityProperty[] properties) {
        this.target = target;
        this.properties = properties;
    }

    @Override
    public boolean testCondition(@NotNull LootContext context) {
        LivingEntity entity = context.getEntity(this.target);
        if (entity == null) {
            return false;
        }
        LootEntityProperty[] properties = this.properties;
        for (LootEntityProperty property : this.properties) {
            if (!property.test(context.getRandom(), entity)) {
                return false;
            }
        }
        return true;
    }

    public static class Serializer extends LootCondition.Serializer<EntityHasProperty> {

        public Serializer() {
            super("entity_properties", EntityHasProperty.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull EntityHasProperty item, @NotNull JsonSerializationContext context) {
            JsonObject properties = new JsonObject();
            for (LootEntityProperty property : item.properties) {
                LootEntityProperty.Serializer<LootEntityProperty> serializer = LootEntityProperties.getSerializerFor(property);
                properties.add(serializer.getName(), serializer.serialize(property, context));
            }
            object.addProperty("entity", item.target.getName());
            object.add("properties", properties);
        }

        @NotNull
        @Override
        public EntityHasProperty deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            Set<Map.Entry<String, JsonElement>> entrySet = JsonUtils.getJsonObject(object, "properties").entrySet();
            LootEntityProperty[] properties = new LootEntityProperty[entrySet.size()];
            int n = 0;
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                properties[n++] = LootEntityProperties.getSerializerForName(entry.getKey()).deserialize(entry.getValue(), context);
            }
            return new EntityHasProperty(LootContext.EntityTarget.getEntityTarget(JsonUtils.getString(object, "entity")), properties);
        }
    }
}
