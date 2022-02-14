package ru.civwars.util.number;

import java.util.Random;
import ru.lib27.annotation.NotNull;

public class RandomDouble {

    private final double min;
    private final double max;

    public RandomDouble(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public RandomDouble(double value) {
        this.min = value;
        this.max = value;
    }

    /**
     * @return минимальное значение.
     */
    public final double getMin() {
        return this.min;
    }

    /**
     * @return максимальное значение.
     */
    public final double getMax() {
        return this.max;
    }

    /**
     * @return {@code true}, если мин./макс. значения равны 0. Иначе
     * {@code false}.
     */
    public boolean isEmpty() {
        return this.min == 0.0D && this.max == 0.0D;
    }

    public boolean isInRange(double value) {
        return value <= this.max && value >= this.min;
    }

    /**
     * @param random
     * @return случайное значение в диапозоне от мин. до макс. значений.
     */
    public double generateDouble(@NotNull Random random) {
        if (this.min >= this.max) {
            return this.min;
        }
        return random.nextDouble() * (this.max - this.min) + this.min;
    }

    @Override
    public int hashCode() {
        return (int) this.min ^ (int) this.max;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof RandomDouble)) {
            return false;
        }

        RandomDouble other = (RandomDouble) object;
        return (this.min == other.min && this.max == other.max);
    }

    @Override
    public String toString() {
        return "RandomDouble{min=" + this.min + ",max=" + this.max + "}";
    }

}
