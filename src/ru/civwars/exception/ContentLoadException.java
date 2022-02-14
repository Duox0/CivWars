package ru.civwars.exception;

import ru.lib27.annotation.NotNull;

public class ContentLoadException extends Exception {
    
    public ContentLoadException() {
    }

    public ContentLoadException(@NotNull String reason) {
        super(reason);
    }

    public ContentLoadException(@NotNull String string, @NotNull Throwable thrwbl) {
        super("Error loading \"" + string + "\"", thrwbl);
    }

    public ContentLoadException(@NotNull Throwable thrwbl) {
        super(thrwbl);
    }

}
