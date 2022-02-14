package ru.civwars.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.loot.LootContext;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;

public class KilledByPlayer extends LootCondition {

    private final boolean inverse;

    public KilledByPlayer(boolean inverse) {
        this.inverse = inverse;
    }

    public KilledByPlayer() {
        this(false);
    }

    @Override
    public boolean testCondition(@NotNull LootContext context) {
        return context.getKillerPlayer() != null == !this.inverse;
    }

    public static class Serializer extends LootCondition.Serializer<KilledByPlayer> {

        public Serializer() {
            super("killed_by_player", KilledByPlayer.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull KilledByPlayer item, @NotNull JsonSerializationContext context) {
            object.addProperty("inverse", item.inverse);
        }

        @NotNull
        @Override
        public KilledByPlayer deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
            return new KilledByPlayer(JsonUtils.getBooleanOrDefault(object, "inverse", false));
        }
    }

}
