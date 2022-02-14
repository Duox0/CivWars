package ru.civwars.schematic.block.command;

import com.google.common.collect.Maps;
import java.util.Map;
import ru.lib27.annotation.NotNull;

public class SchematicCommands {
    
    private static final Map<String, SchematicBlockCommand.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends SchematicBlockCommand>, SchematicBlockCommand.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerBlock(new Chest.Serializer());
    }

    public static <T extends SchematicBlockCommand> void registerBlock(@NotNull SchematicBlockCommand.Serializer<? extends T> serializer) {
        String name = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getBlockClass();
        if (SchematicCommands.NAME_TO_SERIALIZER_MAP.containsKey(name)) {
            throw new IllegalArgumentException("Can't re-register schematic command name " + name);
        }
        if (SchematicCommands.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register schematic command class " + clazz.getName());
        }
        SchematicCommands.NAME_TO_SERIALIZER_MAP.put(name, serializer);
        SchematicCommands.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static SchematicBlockCommand.Serializer<?> getSerializerForName(@NotNull String name) {
        final SchematicBlockCommand.Serializer<?> serializer = SchematicCommands.NAME_TO_SERIALIZER_MAP.get(name);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown schematic command '" + name + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends SchematicBlockCommand> SchematicBlockCommand.Serializer<T> getSerializerFor(@NotNull T sb) {
        SchematicBlockCommand.Serializer<T> serializer = (SchematicBlockCommand.Serializer<T>) SchematicCommands.CLASS_TO_SERIALIZER_MAP.get(sb.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown schematic command " + sb);
        }
        return serializer;
    }
}
