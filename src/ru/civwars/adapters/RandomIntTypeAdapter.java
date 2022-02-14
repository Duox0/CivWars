package ru.civwars.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.number.RandomInt;

public class RandomIntTypeAdapter implements JsonSerializer<RandomInt>, JsonDeserializer<RandomInt> {

    @Override
    public JsonElement serialize(RandomInt item, Type type, JsonSerializationContext context) {
        if (item.getMin() == item.getMax()) {
            return new JsonPrimitive(item.getMin());
        }
        JsonObject object = new JsonObject();
        object.addProperty("min", item.getMin());
        object.addProperty("max", item.getMax());
        return object;
    }

    @Override
    public RandomInt deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (JsonUtils.isNumber(element)) {
            return new RandomInt(JsonUtils.getInt(element, "value"));
        }
        JsonObject object = JsonUtils.getJsonObject(element, "value");
        return new RandomInt(JsonUtils.getInt(object, "min"), JsonUtils.getInt(object, "max"));
    }

}
