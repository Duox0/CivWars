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
import ru.civwars.util.JsonUtils;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.loot.functions.EnchantRandomly;
import ru.civwars.loot.functions.LootFunction;
import ru.civwars.loot.functions.Looting;
import ru.civwars.loot.functions.SetCount;
import ru.civwars.loot.functions.SetDurability;
import ru.civwars.loot.functions.SetMetadata;
import ru.civwars.loot.functions.Smelt;
import ru.lib27.annotation.NotNull;

public class LootFunctionTypeAdapter implements JsonSerializer<LootFunction>, JsonDeserializer<LootFunction> {

    private static final Map<String, LootFunction.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends LootFunction>, LootFunction.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerFunction(new SetCount.Serializer());
        registerFunction(new SetMetadata.Serializer());
        registerFunction(new EnchantRandomly.Serializer());
        registerFunction(new Smelt.Serializer());
        registerFunction(new Looting.Serializer());
        registerFunction(new SetDurability.Serializer());
    }

    public static <T extends LootFunction> void registerFunction(@NotNull LootFunction.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getFunctionClass();
        if (LootFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register loot function type " + type);
        }
        if (LootFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register loot function class " + clazz.getName());
        }
        LootFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        LootFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static LootFunction.Serializer<?> getSerializerForType(@NotNull String type) {
        final LootFunction.Serializer<?> serializer = LootFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot function type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends LootFunction> LootFunction.Serializer<T> getSerializerFor(@NotNull T function) {
        LootFunction.Serializer<T> serializer = (LootFunction.Serializer<T>) LootFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot function " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(LootFunction function, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        if (function.getConditions().length > 0) {
            object.add("conditions", context.serialize(function.getConditions()));
        }

        LootFunction.Serializer<LootFunction> serializer = LootFunctionTypeAdapter.getSerializerFor(function);
        object.addProperty("function", serializer.getName());
        serializer.serialize(object, function, context);

        return object;
    }

    @Override
    public LootFunction deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "loot condition");

        String functionType = JsonUtils.getString(object, "function");
        LootFunction.Serializer<?> serializer;
        try {
            serializer = LootFunctionTypeAdapter.getSerializerForType(functionType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown loot function type '" + functionType + "'");
        }

        LootCondition[] conditions = JsonUtils.deserializeOrDefault(object, "conditions", new LootCondition[0], context, LootCondition[].class);

        return (LootFunction) serializer.deserialize(object, context, conditions);
    }

}
