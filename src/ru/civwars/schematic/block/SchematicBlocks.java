package ru.civwars.schematic.block;

import com.google.common.collect.Maps;
import java.util.Map;
import ru.lib27.annotation.NotNull;

public class SchematicBlocks {
    
    private static final Map<Integer, SchematicBlock.Serializer<?>> ID_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends SchematicBlock>, SchematicBlock.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerBlock(0, new SimpleBlock.Serializer());
        registerBlock(63, new StandingSign.Serializer());
        registerBlock(68, new WallSign.Serializer());
    }

    public static <T extends SchematicBlock> void registerBlock(int blockId, @NotNull SchematicBlock.Serializer<? extends T> serializer) {
        Class<T> clazz = (Class<T>) serializer.getBlockClass();
        if (SchematicBlocks.ID_TO_SERIALIZER_MAP.containsKey(blockId)) {
            throw new IllegalArgumentException("Can't re-register schematic block id " + blockId);
        }
        if (SchematicBlocks.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register schematic block class " + clazz.getName());
        }
        SchematicBlocks.ID_TO_SERIALIZER_MAP.put(blockId, serializer);
        SchematicBlocks.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static SchematicBlock.Serializer<?> getSerializerForName(int blockId) {
        final SchematicBlock.Serializer<?> serializer = SchematicBlocks.ID_TO_SERIALIZER_MAP.get(blockId);
        if (serializer == null) {
            return SchematicBlocks.ID_TO_SERIALIZER_MAP.get(0);
        }
        return serializer;
    }

    @NotNull
    public static <T extends SchematicBlock> SchematicBlock.Serializer<T> getSerializerFor(@NotNull T sb) {
        SchematicBlock.Serializer<T> serializer = (SchematicBlock.Serializer<T>) SchematicBlocks.CLASS_TO_SERIALIZER_MAP.get(sb.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown schematic block " + sb);
        }
        return serializer;
    }
}
