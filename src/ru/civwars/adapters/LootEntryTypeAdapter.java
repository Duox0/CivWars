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
import ru.civwars.loot.LootEntry;
import ru.civwars.loot.LootEntryEmpty;
import ru.civwars.loot.LootEntryItem;
import ru.civwars.loot.LootEntryTable;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class LootEntryTypeAdapter implements JsonSerializer<LootEntry>, JsonDeserializer<LootEntry> {

    private static final Map<String, LootEntry.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends LootEntry>, LootEntry.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerEntry(new LootEntryEmpty.Serializer());
        registerEntry(new LootEntryItem.Serializer());
        registerEntry(new LootEntryTable.Serializer());
    }

    public static <T extends LootEntry> void registerEntry(@NotNull LootEntry.Serializer<? extends T> serializer) {
        String itemType = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getEntryClass();
        if (LootEntryTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(itemType)) {
            throw new IllegalArgumentException("Can't re-register loot entry type " + itemType);
        }
        if (LootEntryTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register loot entry class " + clazz.getName());
        }
        LootEntryTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(itemType, serializer);
        LootEntryTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static LootEntry.Serializer<?> getSerializerForType(@NotNull String baseClass) {
        final LootEntry.Serializer<?> serializer = LootEntryTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(baseClass);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot entry type '" + baseClass + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends LootEntry> LootEntry.Serializer<T> getSerializerFor(@NotNull T function) {
        LootEntry.Serializer<T> serializer = (LootEntry.Serializer<T>) LootEntryTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot entry " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(LootEntry entry, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        LootEntry.Serializer<LootEntry> serializer = LootEntryTypeAdapter.getSerializerFor(entry);

        object.addProperty("weight", entry.getWeight());
        object.addProperty("quality", entry.getQuality());
        if (entry.getConditions().length > 0) {
            object.add("conditions", context.serialize(entry.getConditions()));
        }
        if (entry instanceof LootEntryItem) {
            object.addProperty("type", "minecraft_item");
        } else if (entry instanceof LootEntryTable) {
            object.addProperty("type", "loot_table");
        } else {
            if (!(entry instanceof LootEntryEmpty)) {
                throw new IllegalArgumentException("Don't know how to serialize " + entry);
            }
            object.addProperty("type", "empty");
        }

        serializer.serialize(object, entry, context);
        return object;
    }

    @Override
    public LootEntry deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "loot entry");
        String baseClass = JsonUtils.getString(object, "type");
        LootEntry.Serializer<?> serializer;
        try {
            serializer = LootEntryTypeAdapter.getSerializerForType(baseClass);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown loot entry type '" + baseClass + "'");
        }

        int weight = JsonUtils.getIntOrDefault(object, "weight", 1);
        int quality = JsonUtils.getIntOrDefault(object, "quality", 0);
        LootCondition[] conditions = JsonUtils.deserializeOrDefault(object, "conditions", new LootCondition[0], context, LootCondition[].class);

        return serializer.deserialize(object, context, weight, quality, conditions);
    }

}
