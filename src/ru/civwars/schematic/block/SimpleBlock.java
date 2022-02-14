package ru.civwars.schematic.block;

import ru.lib27.annotation.NotNull;

public class SimpleBlock extends SchematicBlock {

    public SimpleBlock(int blockId, byte blockData) {
        super(blockId, blockData);
    }
    
    public static class Serializer extends SchematicBlock.Serializer<SimpleBlock> {

        protected Serializer() {
            super(SimpleBlock.class);
        }

        @Override
        public String serialize(@NotNull SimpleBlock item) {
            return "";
        }

        @NotNull
        @Override
        public SimpleBlock deserialize(int blockId, byte blockData, @NotNull String[] args) {
            return new SimpleBlock(blockId, blockData);
        }
    }
}
