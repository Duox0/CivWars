package ru.civwars.util;

import ru.lib27.annotation.Nullable;

public class Result<T> {

    private int error;
    private T result;

    public Result() {
        this.error = 0;
        this.result = null;
    }

    public Result setError(int error) {
        this.error = error;
        return this;
    }
    
    public int error() {
        return this.error;
    }
    
    public Result setResult(@Nullable T result) {
        this.result = result;
        return this;
    }

    @Nullable
    public T result() {
        return this.result;
    }

}
