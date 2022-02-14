package ru.civwars.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import ru.civwars.bukkit.entity.BukkitEntityType;
import ru.civwars.bukkit.entity.BukkitEntityTypes;
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.util.JsonUtils;

public class NpcDataTypeAdapter implements JsonSerializer<NPCTemplate>, JsonDeserializer<NPCTemplate> {

    @Override
    public JsonElement serialize(NPCTemplate data, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        return object;
    }

    @Override
    public NPCTemplate deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "npc data");

        int id = JsonUtils.getInt(object, "id");
        String name = JsonUtils.getString(object, "name");

        String entityTypeId = JsonUtils.getString(object, "entity_type");
        BukkitEntityTypes entityType = BukkitEntityTypes.getEntityType(entityTypeId);
        if (entityType == null) {
            throw new JsonSyntaxException("Unknown bukkit entity type '" + entityTypeId + "'");
        }

        BukkitEntityType entityTypeInst = entityType.getSerializer().deserialize(JsonUtils.getJsonObjectOrDefault(object, "options", new JsonObject()), context);

        NPCTemplate.Property property = new NPCTemplate.Property(id, name, entityTypeInst);

        property.baseHealth(JsonUtils.getDoubleOrDefault(object, "base_health", 20.0D));
        property.baseDefense(JsonUtils.getDoubleOrDefault(object, "base_defense", 0.0D));
        property.baseDefenseToughness(JsonUtils.getDoubleOrDefault(object, "base_defense_toughness", 0.0D));
        property.baseAttackDamage(JsonUtils.getDoubleOrDefault(object, "base_attack_damage", 2.0D));
        property.baseAttackSpeed(JsonUtils.getDoubleOrDefault(object, "base_attack_speed", 4.0D));
        property.baseMovementSpeed(JsonUtils.getDoubleOrDefault(object, "base_movement_speed", 0.7D));
        property.baseFlyingSpeed(JsonUtils.getDoubleOrDefault(object, "base_flying_speed", 0.4D));
        property.baseKnockbackResistance(JsonUtils.getDoubleOrDefault(object, "base_knockback_resistance", 0.0D));

        property.baseFollowRange(JsonUtils.getDoubleOrDefault(object, "base_follow_range", 16.0D));

        return new NPCTemplate(property);
    }

}
