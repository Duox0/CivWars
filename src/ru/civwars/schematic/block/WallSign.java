package ru.civwars.schematic.block;

import ru.lib27.annotation.NotNull;

public class WallSign extends Sign {

    public WallSign(byte blockData, @NotNull String[] lines) {
        super(68, blockData, lines);
    }

    public static class Serializer extends Sign.Serializer<WallSign> {

        protected Serializer() {
            super(WallSign.class);
        }

        @Override
        public String serialize(@NotNull WallSign item) {
            return "";
        }
        
        @NotNull
        @Override
        public WallSign deserialize(byte blockData, @NotNull String[] lines) {
            return new WallSign(blockData, lines);
        }
    }
}
