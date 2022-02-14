package ru.civwars.schematic.block;

import ru.lib27.annotation.NotNull;

public class StandingSign extends Sign {

    public StandingSign(byte blockData, @NotNull String[] lines) {
        super(63, blockData, lines);
    }

    public static class Serializer extends Sign.Serializer<StandingSign> {

        protected Serializer() {
            super(StandingSign.class);
        }

        @Override
        public String serialize(@NotNull StandingSign item) {
            return "";
        }
        
        @NotNull
        @Override
        public StandingSign deserialize(byte blockData, @NotNull String[] lines) {
            return new StandingSign(blockData, lines);
        }
    }
}
