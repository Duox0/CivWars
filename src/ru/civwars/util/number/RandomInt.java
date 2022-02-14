package ru.civwars.util.number;

import java.util.Random;
import ru.lib27.annotation.NotNull;

public class RandomInt {

    private final int min;
    private final int max;

    public RandomInt(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public RandomInt(int value) {
        this.min = value;
        this.max = value;
    }

    /**
     * @return минимальное значение.
     */
    public final int getMin() {
        return this.min;
    }

    /**
     * @return максимальное значение.
     */
    public final int getMax() {
        return this.max;
    }

    /**
     * @return {@code true}, если мин./макс. значения равны 0. Иначе
     * {@code false}.
     */
    public boolean isEmpty() {
        return this.min == 0 && this.max == 0;
    }

    public boolean isInRange(int value) {
        return value <= this.max && value >= this.min;
    }

    /**
     * @param random
     * @return случайное значение в диапозоне от мин. до макс. значений.
     */
    public int generateInt(@NotNull Random random) {
        if (this.min >= this.max) {
            return this.min;
        }
        return random.nextInt(this.max - this.min + 1) + this.min;
    }

    @Override
    public int hashCode() {
        return this.min ^ this.max;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof RandomInt)) {
            return false;
        }

        RandomInt other = (RandomInt) object;
        return (this.min == other.min && this.max == other.max);
    }

    @Override
    public String toString() {
        return "RandomInt{min=" + this.min + ",max=" + this.max + "}";
    }

}
