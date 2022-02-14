package ru.civwars.schematic.block;

import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public abstract class SchematicBlock {

    public static final SchematicBlock AIR = new SimpleBlock(0, (byte) 0);

    private final int blockId;
    private final byte blockData;

    public SchematicBlock(int blockId, byte blockData) {
        this.blockId = blockId;
        this.blockData = blockData;
    }

    public final int getBlockId() {
        return this.blockId;
    }

    public final byte getBlockData() {
        return this.blockData;
    }
    
    public boolean isCommand() {
        return false;
    }

    public void setBlock(@NotNull Block block) {
        block.setTypeIdAndData(this.getBlockId(), this.getBlockData(), true);
    }
    
    public abstract static class Serializer<T extends SchematicBlock> {

        private final Class<T> clazz;

        protected Serializer(@NotNull Class<T> clazz) {
            this.clazz = clazz;
        }

        @NotNull
        public final Class<T> getBlockClass() {
            return this.clazz;
        }

        public abstract String serialize(@NotNull T item);

        @NotNull
        public abstract T deserialize(int blockId, byte blockData, @NotNull String[] args);
    }
}
