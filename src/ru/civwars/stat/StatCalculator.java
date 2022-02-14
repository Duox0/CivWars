package ru.civwars.stat;

import com.google.common.collect.Lists;
import java.util.List;
import ru.civwars.stat.functions.StatFunction;
import ru.lib27.annotation.NotNull;

public final class StatCalculator {

    /* Empty Functions table definition. */
    private static final StatFunction[] EMPTY_FUNCTIONS = new StatFunction[0];

    /* Table of Functions object. */
    private StatFunction[] functions;

    public StatCalculator(@NotNull StatCalculator calculator) {
        this.functions = calculator.functions;
    }

    public StatCalculator() {
        this.functions = EMPTY_FUNCTIONS;
    }

    /**
     * Добавляет функцию.
     *
     * @param function - функция к добавлению.
     */
    public synchronized void addFunction(@NotNull StatFunction function) {
        StatFunction[] functions = this.functions;
        StatFunction[] tmp = new StatFunction[functions.length + 1];

        final int priority = function.getPriority();
        int i = 0;

        for (; (i < functions.length) && (priority >= functions[i].getPriority()); i++) {
            tmp[i] = functions[i];
        }

        tmp[i] = function;

        for (; i < functions.length; i++) {
            tmp[i + 1] = functions[i];
        }

        this.functions = tmp;
    }

    /**
     * Удаляет функцию.
     *
     * @param function - функция к удалению.
     */
    public synchronized void removeFunction(@NotNull StatFunction function) {
        StatFunction[] functions = this.functions;
        StatFunction[] tmp = new StatFunction[functions.length - 1];

        int i = 0;

        for (; (i < functions.length) && (function != functions[i]); i++) {
            tmp[i] = functions[i];
        }

        if (i == functions.length) {
            return;
        }

        for (i++; i < functions.length; i++) {
            tmp[i - 1] = functions[i];
        }

        if (tmp.length == 0) {
            this.functions = EMPTY_FUNCTIONS;
        } else {
            this.functions = tmp;
        }
    }

    /**
     * Удаляет функции, источником которых является #source.
     *
     * @param source - источник функции.
     * @return
     */
    public synchronized List<Stats> removeSource(@NotNull Object source) {
        StatFunction[] functions = this.functions;
        List<Stats> modifiedStats = Lists.newArrayList();

        for (StatFunction function : functions) {
            if (function.getSource() == source) {
                modifiedStats.add(function.getStat());
                this.removeFunction(function);
            }
        }
        return modifiedStats;
    }

    public int size() {
        return this.functions.length;
    }

    public void calculate(@NotNull StatContext context) {
        StatFunction[] functions = this.functions;

        for (StatFunction function : functions) {
            function.calculate(context);
        }
    }

    public static boolean equalsCals(StatCalculator calc1, StatCalculator calc2) {
        if (calc1 == calc2) {
            return true;
        }

        if ((calc1 == null) || (calc2 == null)) {
            return false;
        }

        StatFunction[] functions1 = calc1.functions;
        StatFunction[] functions2 = calc2.functions;

        if (functions1 == functions2) {
            return true;
        }

        if (functions1.length != functions2.length) {
            return false;
        }

        if (functions1.length == 0) {
            return true;
        }

        for (int i = 0; i < functions1.length; i++) {
            if (functions1[i] != functions2[i]) {
                return false;
            }
        }
        return true;
    }
}
