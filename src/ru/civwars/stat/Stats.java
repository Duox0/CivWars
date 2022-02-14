package ru.civwars.stat;

import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public enum Stats {

    ATTACK_DAMAGE("attack_damage"),
    DEFENSE("defense"),
    MOVEMENT_SPEED("movement_speed", -1.0D, 1.0D);

    public static int NUM_STATS = values().length;

    private final String name;
    private final double minValue;
    private final double maxValue;

    private Stats(@NotNull String name, double minValue, double maxValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    private Stats(@NotNull String name, double minValue) {
        this(name, minValue, Double.MAX_VALUE);
    }
    
    private Stats(@NotNull String name) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @NotNull
    public String getName() {
        return this.name;
    }
    
    public double getMinValue() {
        return this.minValue;
    }
    
    public double getMaxValue() {
        return this.maxValue;
    }

    @Nullable
    public static Stats getStat(@NotNull String name) {
        for (Stats stat : values()) {
            if (stat.getName().equalsIgnoreCase(name)) {
                return stat;
            }
        }
        return null;
    }
}
