package ru.civwars.stat.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import ru.civwars.stat.StatContext;
import ru.civwars.stat.Stats;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class StatFunction {

    protected final Stats stat;

    /* Приоритет функции. */
    protected final int priority;
    
    /* Источник функции. */
    protected final Object source;

    public StatFunction(@NotNull Stats stat, int priority, @Nullable Object source) {
        this.stat = stat;
        this.priority = priority;
        this.source = source;
    }

    @NotNull
    public Stats getStat() {
        return this.stat;
    }
    
    /**
     * Получает приоритет функции.
     * @return 
     */
    public int getPriority() {
        return this.priority;
    }
    
    /**
     * Получает источник функции.
     * @return 
     */
    @Nullable
    public Object getSource() {
        return this.source;
    }
    
    @NotNull
    public abstract StatFunction createInstance(@Nullable Object owner);
    
    /**
     * Run the mathematic function.
     *
     * @param context
     */
    public abstract void calculate(@NotNull StatContext context);

    public abstract static class Serializer<T extends StatFunction> {

        private final String name;
        private final Class<T> clazz;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getFunctionClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull Stats stat, int priority);
    }
    
}
