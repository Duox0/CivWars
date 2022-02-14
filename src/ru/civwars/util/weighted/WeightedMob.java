package ru.civwars.util.weighted;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class WeightedMob extends WeightedRandomItem {

    public static final WeightedMob.Serializer SERIALIZER = new WeightedMob.Serializer();
    
    private final int mobId;

    public WeightedMob(int mobId, int weight) {
        super(weight);
        this.mobId = mobId;
    }

    public int getMobId() {
        return this.mobId;
    }

    public static class Serializer extends WeightedRandomItem.Serializer<WeightedMob> {

        public Serializer() {
            super("mob", WeightedMob.class);
        }

        @NotNull
        @Override
        public JsonElement serialize(@NotNull WeightedMob item, @NotNull JsonSerializationContext context) {
            return new JsonObject();
        }

        @NotNull
        @Override
        public WeightedMob deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new WeightedMob(JsonUtils.getInt(object, "mob_id"), JsonUtils.getIntOrDefault(object, "weight", 1));
        }
    }
}
