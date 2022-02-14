package ru.civwars.util;

import ru.lib27.annotation.NotNull;

public class Size3i {

    public static final Size3i SIZE_NONE = null;

    public final int width;
    public final int height;
    public final int length;

    public Size3i(int width, int heigth, int length) {
        if (width < 0) {
            throw new IllegalArgumentException("Width should be positive");
        }
        if (heigth < 0) {
            throw new IllegalArgumentException("Height should be positive");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length should be positive");
        }

        this.width = width;
        this.height = heigth;
        this.length = length;
    }

    public Size3i(@NotNull Size3i size) {
        this(size.width, size.height, size.length);
    }

    @Override
    public int hashCode() {
        return this.width ^ this.height;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof Size3i)) {
            return false;
        }

        Size3i other = (Size3i) object;
        return (this.width == other.width && this.height == other.height && this.length == other.length);
    }

    @Override
    public String toString() {
        return "Size{width=" + this.width + ",height=" + this.height + ",length=" + this.length + "}";
    }

    public boolean isEmpty() {
        return (this.width == 0 && this.height == 0 && this.length == 0);
    }

}
