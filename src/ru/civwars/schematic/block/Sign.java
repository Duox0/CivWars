package ru.civwars.schematic.block;

import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public class Sign extends SchematicBlock {

    private final String[] lines;

    public Sign(int blockId, byte blockData, @NotNull String[] lines) {
        super(blockId, blockData);
        this.lines = lines;
    }

    @Override
    public void setBlock(@NotNull Block block) {
        super.setBlock(block);
        if (block.getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
            for (int length = this.lines.length, i = 0; i < 4 && i < length; i++) {
                sign.setLine(i, this.lines[i]);
            }
            sign.update();
        }
    }

    public abstract static class Serializer<T extends Sign> extends SchematicBlock.Serializer<T> {

        protected Serializer(@NotNull Class<T> clazz) {
            super(clazz);
        }

        @Override
        public String serialize(@NotNull Sign item) {
            return "";
        }

        @NotNull
        @Override
        public final T deserialize(int blockId, byte blockData, @NotNull String[] args) {
            String[] lines = new String[args.length];
            for (int length = args.length, i = 0; i < 4 && i < length; i++) {
                lines[i] = args[i];
            }
            return this.deserialize(blockData, lines);
        }

        @NotNull
        public abstract T deserialize(byte blockData, @NotNull String[] lines);
    }
}
