package ru.civwars.entity.template;

import ru.lib27.annotation.NotNull;

public class PlayerTemplate extends BasicEntityTemplate {

    public PlayerTemplate(@NotNull Property property) {
        super(property);
    }

    public static class Property extends BasicEntityTemplate.Property {

        public Property(int id, @NotNull String name) {
            super(id, name);
        }

    }
}
