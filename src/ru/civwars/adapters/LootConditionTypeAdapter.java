package ru.civwars.adapters;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import ru.civwars.loot.conditions.*;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class LootConditionTypeAdapter implements JsonSerializer<LootCondition>, JsonDeserializer<LootCondition> {

    private static final Map<String, LootCondition.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends LootCondition>, LootCondition.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerCondition(new RandomChance.Serializer());
        registerCondition(new RandomChanceWithLooting.Serializer());
        registerCondition(new EntityHasProperty.Serializer());
        registerCondition(new KilledByPlayer.Serializer());
    }

    public static <T extends LootCondition> void registerCondition(@NotNull LootCondition.Serializer<? extends T> serializer) {
        String name = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getConditionClass();
        if (LootConditionTypeAdapter.NAME_TO_SERIALIZER_MAP.containsKey(name)) {
            throw new IllegalArgumentException("Can't re-register loot condition type " + name);
        }
        if (LootConditionTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register loot condition class " + clazz.getName());
        }
        LootConditionTypeAdapter.NAME_TO_SERIALIZER_MAP.put(name, serializer);
        LootConditionTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static LootCondition.Serializer<?> getSerializerForName(@NotNull String name) {
        LootCondition.Serializer<?> serializer = LootConditionTypeAdapter.NAME_TO_SERIALIZER_MAP.get(name);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot condition type '" + name + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends LootCondition> LootCondition.Serializer<T> getSerializerFor(@NotNull T condition) {
        LootCondition.Serializer<T> serializer = (LootCondition.Serializer<T>) LootConditionTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(condition.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot condition " + condition);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(LootCondition condition, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        LootCondition.Serializer<LootCondition> serializer = LootConditionTypeAdapter.getSerializerFor(condition);
        object.addProperty("condition", serializer.getName());
        serializer.serialize(object, condition, context);

        return object;
    }

    @Override
    public LootCondition deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "loot condition");
        String baseClass = JsonUtils.getString(object, "condition");
        LootCondition.Serializer<?> serializer;
        try {
            serializer = LootConditionTypeAdapter.getSerializerForName(baseClass);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown loot condition type '" + baseClass + "'");
        }
        return (LootCondition) serializer.deserialize(object, context);
    }

}
