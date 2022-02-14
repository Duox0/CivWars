package ru.civwars.util;

import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class InteractionResult<T> {

    private final EnumInteractionResult type;
    private final T result;

    public InteractionResult(@NotNull EnumInteractionResult type, @Nullable T result) {
        this.type = type;
        this.result = result;
    }

    @NotNull
    public EnumInteractionResult getType() {
        return this.type;
    }

    @NotNull
    public T getResult() {
        return this.result;
    }
}
