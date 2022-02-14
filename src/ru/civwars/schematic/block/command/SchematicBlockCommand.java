package ru.civwars.schematic.block.command;

import org.bukkit.block.Block;
import ru.civwars.building.Building;
import ru.civwars.schematic.block.SchematicBlock;
import ru.lib27.annotation.NotNull;

public abstract class SchematicBlockCommand extends SchematicBlock {

    public SchematicBlockCommand() {
        super(0, (byte) 0);
    }

    @Override
    public final boolean isCommand() {
        return true;
    }

    public abstract void onPostBuild(@NotNull Building building, @NotNull Block block);

    public abstract static class Serializer<T extends SchematicBlockCommand> extends SchematicBlock.Serializer<T> {

        private final String name;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            super(clazz);
            this.name = name;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        @Override
        public final T deserialize(int blockId, byte blockData, @NotNull String[] args) {
            return this.deserialize(args);
        }

        @NotNull
        protected abstract T deserialize(@NotNull String[] args);
    }
}
