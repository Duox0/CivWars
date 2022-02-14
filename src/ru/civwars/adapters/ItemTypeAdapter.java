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
import org.bukkit.Material;
import ru.civwars.item.CustomItem;
import ru.civwars.item.armor.*;
import ru.civwars.item.special.*;
import ru.civwars.item.weapon.*;
import ru.civwars.stat.functions.StatFunction;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class ItemTypeAdapter implements JsonSerializer<CustomItem>, JsonDeserializer<CustomItem> {

    private static final Map<String, CustomItem.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends CustomItem>, CustomItem.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerItem(new CustomItem.Serializer<CustomItem>("item", CustomItem.class) {

            @NotNull
            @Override
            public void serialize(@NotNull JsonObject object, @NotNull CustomItem item, @NotNull JsonSerializationContext context) {
            }

            @NotNull
            @Override
            public CustomItem deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull CustomItem.Property property) {
                return new CustomItem(property);
            }
        });

        registerItem(new ItemSword.Serializer());
        registerItem(new FireSword.Serializer());

        registerItem(new ItemHelmet.Serializer());
        registerItem(new ItemChestplate.Serializer());
        registerItem(new ItemLeggings.Serializer());
        registerItem(new ItemBoots.Serializer());
        registerItem(new ItemShield.Serializer());

        registerItem(new ItemFactionFlag.Serializer());
        registerItem(new ItemSettlement.Serializer());
    }

    public static <T extends CustomItem> void registerItem(@NotNull CustomItem.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getConditionClass();
        if (ItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register custom item type " + type);
        }
        if (ItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register custom item class " + clazz.getName());
        }
        ItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        ItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static CustomItem.Serializer<?> getSerializerForType(@NotNull String type) {
        final CustomItem.Serializer<?> serializer = ItemTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown custom item type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends CustomItem> CustomItem.Serializer<T> getSerializerFor(@NotNull T function) {
        CustomItem.Serializer<T> serializer = (CustomItem.Serializer<T>) ItemTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown custom item " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(CustomItem item, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        CustomItem.Serializer<CustomItem> serializer = ItemTypeAdapter.getSerializerFor(item);
        object.addProperty("type", serializer.getName());

        object.addProperty("id", item.getId());
        object.addProperty("name", item.getName());
        serializer.serialize(object, item, context);

        return object;
    }

    @Override
    public CustomItem deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "custom item");

        String itemType = JsonUtils.getString(object, "type");
        CustomItem.Serializer<?> serializer;
        try {
            serializer = ItemTypeAdapter.getSerializerForType(itemType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown custom item type '" + itemType + "'");
        }

        int id = JsonUtils.getInt(object, "id");
        String name = JsonUtils.getString(object, "name");
        Material item = JsonUtils.deserialize(object, "item_id", context, Material.class);
        short data = JsonUtils.getShortOrDefault(object, "metadata", (short) 0);

        CustomItem.Property property = new CustomItem.Property(id, name, item, data);

        property.statFunctions(JsonUtils.deserializeOrDefault(object, "stats", CustomItem.EMPTY_FUNCTIONS, context, StatFunction[].class));

        return (CustomItem) serializer.deserialize(object, context, property);
    }

}
