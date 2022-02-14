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
import ru.civwars.util.item.RequiredCustomItem;
import ru.civwars.util.item.RequiredItem;
import ru.civwars.util.item.RequiredVanillaItem;
import ru.lib27.annotation.NotNull;

public class RequiredItemTypeAdapter implements JsonSerializer<RequiredItem>, JsonDeserializer<RequiredItem> {

    private static final Map<String, RequiredItem.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends RequiredItem>, RequiredItem.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerItem(new RequiredVanillaItem.Serializer());
        registerItem(new RequiredCustomItem.Serializer());
    }

    public static <T extends RequiredItem> void registerItem(@NotNull RequiredItem.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getItemClass();
        if (RequiredItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register required item type " + type);
        }
        if (RequiredItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register required item class " + clazz.getName());
        }
        RequiredItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        RequiredItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static RequiredItem.Serializer<?> getSerializerForType(@NotNull String type) {
        RequiredItem.Serializer<?> serializer = RequiredItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown required item type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends RequiredItem> RequiredItem.Serializer<T> getSerializerFor(@NotNull T function) {
        RequiredItem.Serializer<T> serializer = (RequiredItem.Serializer<T>) RequiredItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown required item " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(RequiredItem function, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        RequiredItem.Serializer<RequiredItem> serializer = RequiredItemTypeAdapter.getSerializerFor(function);
        object.addProperty("type", serializer.getName());
        serializer.serialize(object, function, context);

        return object;
    }

    @Override
    public RequiredItem deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "required item");
        
        String itemType = JsonUtils.getString(object, "type");
        RequiredItem.Serializer<?> serializer;
        try {
            serializer = RequiredItemTypeAdapter.getSerializerForType(itemType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown required item type '" + itemType + "'");
        }

        return (RequiredItem) serializer.deserialize(object, context, JsonUtils.getIntOrDefault(object, "count", 1));
    }

}
