package ru.civwars.loot.properties;

import com.google.common.collect.Maps;
import java.util.Map;
import ru.lib27.annotation.NotNull;

public class LootEntityProperties {
    
    private static final Map<String, LootEntityProperty.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends LootEntityProperty>, LootEntityProperty.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerProperty(new EntityOnFire.Serializer());
    }

    public static <T extends LootEntityProperty> void registerProperty(@NotNull LootEntityProperty.Serializer<? extends T> serializer) {
        String name = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getConditionClass();
        if (LootEntityProperties.NAME_TO_SERIALIZER_MAP.containsKey(name)) {
            throw new IllegalArgumentException("Can't re-register loot entity property name " + name);
        }
        if (LootEntityProperties.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register loot entity property class " + clazz.getName());
        }
        LootEntityProperties.NAME_TO_SERIALIZER_MAP.put(name, serializer);
        LootEntityProperties.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static LootEntityProperty.Serializer<?> getSerializerForName(@NotNull String name) {
        final LootEntityProperty.Serializer<?> serializer = LootEntityProperties.NAME_TO_SERIALIZER_MAP.get(name);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot entity property '" + name + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends LootEntityProperty> LootEntityProperty.Serializer<T> getSerializerFor(@NotNull T property) {
        LootEntityProperty.Serializer<T> serializer = (LootEntityProperty.Serializer<T>) LootEntityProperties.CLASS_TO_SERIALIZER_MAP.get(property.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown loot entity property " + property);
        }
        return serializer;
    }

}
