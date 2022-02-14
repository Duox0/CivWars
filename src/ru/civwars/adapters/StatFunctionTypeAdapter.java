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
import ru.civwars.stat.Stats;
import ru.civwars.stat.functions.StatFunction;
import ru.civwars.stat.functions.StatFunctionAdd;
import ru.civwars.stat.functions.StatFunctionDiv;
import ru.civwars.stat.functions.StatFunctionMul;
import ru.civwars.stat.functions.StatFunctionSet;
import ru.civwars.stat.functions.StatFunctionSub;
import ru.lib27.annotation.NotNull;

public class StatFunctionTypeAdapter implements JsonSerializer<StatFunction>, JsonDeserializer<StatFunction> {

    private static final Map<String, StatFunction.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends StatFunction>, StatFunction.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerFunction(new StatFunctionAdd.Serializer());
        registerFunction(new StatFunctionSub.Serializer());
        registerFunction(new StatFunctionMul.Serializer());
        registerFunction(new StatFunctionDiv.Serializer());
        registerFunction(new StatFunctionSet.Serializer());
    }

    public static <T extends StatFunction> void registerFunction(@NotNull StatFunction.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getFunctionClass();
        if (StatFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register stat function type " + type);
        }
        if (StatFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register stat function class " + clazz.getName());
        }
        StatFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        StatFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static StatFunction.Serializer<?> getSerializerForType(@NotNull String type) {
        final StatFunction.Serializer<?> serializer = StatFunctionTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown stat function type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends StatFunction> StatFunction.Serializer<T> getSerializerFor(@NotNull T function) {
        StatFunction.Serializer<T> serializer = (StatFunction.Serializer<T>) StatFunctionTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown stat function " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(StatFunction function, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        StatFunction.Serializer<StatFunction> serializer = StatFunctionTypeAdapter.getSerializerFor(function);
        object.addProperty("function", serializer.getName());
        serializer.serialize(object, function, context);

        return object;
    }

    @Override
    public StatFunction deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "stat function");

        String functionType = JsonUtils.getString(object, "function");
        StatFunction.Serializer<?> serializer;
        try {
            serializer = StatFunctionTypeAdapter.getSerializerForType(functionType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown stat function type '" + functionType + "'");
        }

        Stats stat = Stats.getStat(JsonUtils.getString(object, "stat"));
        if (stat == null) {
            throw new JsonSyntaxException("Unknown stat '" + JsonUtils.getString(object, "stat") + "'");
        }

        int priority = JsonUtils.getInt(object, "priority");

        return (StatFunction) serializer.deserialize(object, context, stat, priority);
    }

}
