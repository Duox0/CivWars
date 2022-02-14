package ru.civwars.util.math;

import java.util.Random;
import ru.lib27.annotation.NotNull;

public class MathHelper {

    public static int floor(double value) {
        int value2 = (int) value;
        return (value < value2) ? (value2 - 1) : value2;
    }

    public static int nextInt(@NotNull Random random, int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }
    
    public static double nextDouble(@NotNull Random random, double min, double max) {
        if (min >= max) {
            return min;
        }
        return random.nextDouble() * (max - min) + min;
    }
    
    public static int abs(int i) {
        return (i >= 0) ? i : (-i);
    }

}
