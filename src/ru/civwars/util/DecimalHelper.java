package ru.civwars.util;

import java.text.DecimalFormat;
import ru.lib27.annotation.NotNull;

public class DecimalHelper {
    
    private static final DecimalFormat FORMAT_PERCENTAGE = new DecimalFormat("0.0#");
    
    private DecimalHelper() {
    }

    @NotNull
    public static String formatPercentage(double value) {
        return FORMAT_PERCENTAGE.format(value * 100);
    }
}
