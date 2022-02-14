package ru.civwars.schematic;

import ru.lib27.annotation.NotNull;

public class SchematicException extends Exception {
    
    public SchematicException() {
    }

    public SchematicException(@NotNull String reason) {
        super(reason);
    }

    public SchematicException(@NotNull String string, @NotNull Throwable thrwbl) {
        super("Error loading \"" + string + "\"", thrwbl);
    }

    public SchematicException(@NotNull Throwable thrwbl) {
        super(thrwbl);
    }

}
