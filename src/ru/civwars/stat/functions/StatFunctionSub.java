package ru.civwars.stat.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.stat.StatContext;
import ru.civwars.stat.Stats;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class StatFunctionSub extends StatFunction {

    private final double value;
    
    public StatFunctionSub(@NotNull Stats stat, int priority, double value, @Nullable Object owner) {
        super(stat, priority, owner);
        this.value = value;
    }

    @NotNull
    @Override
    public StatFunction createInstance(@Nullable Object owner) {
        return new StatFunctionSub(this.stat, this.priority, this.value, owner);
    }

    @Override
    public void calculate(@NotNull StatContext context) {
        context.value -= this.value;
    }

    public static class Serializer extends StatFunction.Serializer<StatFunctionSub> {

        public Serializer() {
            super("sub", StatFunctionSub.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull StatFunctionSub item, @NotNull JsonSerializationContext context) {
            object.add("value", context.serialize(item.value));
        }

        @NotNull
        @Override
        public StatFunctionSub deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull Stats stat, int priority) {
            return new StatFunctionSub(stat, priority, JsonUtils.getDouble(object, "value"), null);
        }
    }
}
