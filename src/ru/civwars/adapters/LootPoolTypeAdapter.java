package ru.civwars.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.apache.commons.lang3.ArrayUtils;
import ru.civwars.loot.LootEntry;
import ru.civwars.loot.LootPool;
import ru.civwars.loot.conditions.LootCondition;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.number.RandomInt;

public class LootPoolTypeAdapter implements JsonSerializer<LootPool>, JsonDeserializer<LootPool> {

    @Override
    public JsonElement serialize(LootPool pool, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (!ArrayUtils.isEmpty(pool.getConditions())) {
            object.add("conditions", context.serialize(pool.getConditions()));
        }
        object.add("rolls", context.serialize(pool.getRolls()));
        if (!pool.getBonusRolls().isEmpty()) {
            object.add("bonus_rolls", context.serialize(pool.getBonusRolls()));
        }
        object.add("entries", context.serialize(pool.getEntries()));
        return object;
    }

    @Override
    public LootPool deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "loot pool");
        return new LootPool(JsonUtils.deserialize(object, "entries", context, LootEntry[].class),
                JsonUtils.deserializeOrDefault(object, "conditions", new LootCondition[0], context, LootCondition[].class),
                JsonUtils.deserialize(object, "rolls", context, RandomInt.class),
                JsonUtils.deserializeOrDefault(object, "bonus_rolls", new RandomInt(0), context, RandomInt.class));
    }

}
