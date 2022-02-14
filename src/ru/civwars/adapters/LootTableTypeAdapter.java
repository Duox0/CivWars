package ru.civwars.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import ru.civwars.loot.LootPool;
import ru.civwars.loot.LootTable;
import ru.civwars.util.JsonUtils;

public class LootTableTypeAdapter implements JsonSerializer<LootTable>, JsonDeserializer<LootTable> {

    @Override
    public JsonElement serialize(LootTable table, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("pools", context.serialize(table.getPools()));
        return object;
    }

    @Override
    public LootTable deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "loot pool");
        return new LootTable(JsonUtils.deserializeOrDefault(JsonUtils.getJsonObject(element, "loot table"), "pools", new LootPool[0], context, LootPool[].class));
    }

}
