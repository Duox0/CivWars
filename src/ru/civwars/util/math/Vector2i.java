package ru.civwars.util.math;

public class Vector2i {

    public final static Vector2i X = new Vector2i(1, 0);
    public final static Vector2i Y = new Vector2i(0, 1);
    public final static Vector2i ZERO = new Vector2i(0, 0);

    /* The x-component of this vector. */
    public int x;
    /* The y-component of this vector. */
    public int y;

    public Vector2i() {
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.x;
        result = prime * result + this.y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        Vector2i other = (Vector2i) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
