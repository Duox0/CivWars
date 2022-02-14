package ru.civwars.stat;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.attribute.Attribute;
import ru.civwars.entity.BasicEntity;
import ru.civwars.entity.NPCEntity;
import ru.civwars.stat.functions.StatFunction;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BasicStat {

    /* Table of calculators containing all standard NPC calculator. */
    private static final StatCalculator[] NPC_CALCULATORS;

    static {
        NPC_CALCULATORS = new StatCalculator[Stats.NUM_STATS];
    }

    private final BasicEntity entity;

    /* Table of StatCalculator containing all used calculator. */
    private StatCalculator[] calculators;

    public BasicStat(@NotNull BasicEntity entity) {
        this.entity = entity;

        if (entity instanceof NPCEntity) {
            this.calculators = NPC_CALCULATORS;
        } else {
            this.calculators = new StatCalculator[Stats.NUM_STATS];
        }
    }

    @NotNull
    public BasicEntity getEntity() {
        return this.entity;
    }

    /**
     * Добавляет функциию.
     *
     * @param function - функция к добавлению.
     */
    public final synchronized void addStatFunction(@NotNull StatFunction function) {
        if (this.calculators == NPC_CALCULATORS) {
            this.calculators = new StatCalculator[Stats.NUM_STATS];

            for (int i = 0; i < Stats.NUM_STATS; i++) {
                if (NPC_CALCULATORS[i] != null) {
                    this.calculators[i] = new StatCalculator(NPC_CALCULATORS[i]);
                }
            }
        }

        int statId = function.getStat().ordinal();

        if (this.calculators[statId] == null) {
            this.calculators[statId] = new StatCalculator();
        }

        this.calculators[statId].addFunction(function);

    }

    /**
     * Добавляет функции.
     *
     * @param functions - функции к добавлению.
     */
    public final synchronized void addStatFunctions(@NotNull StatFunction[] functions) {
        List<Stats> modifiedStats = Lists.newArrayList();
        for (StatFunction function : functions) {
            modifiedStats.add(function.getStat());
            this.addStatFunction(function);
        }

        this.broadcastModifiedStats(modifiedStats);
    }

    /**
     * Удаляет функцию.
     *
     * @param function - функция к удалению.
     */
    private synchronized void removeStatFunction(@NotNull StatFunction function) {
        int statId = function.getStat().ordinal();

        if (this.calculators[statId] == null) {
            return;
        }

        this.calculators[statId].removeFunction(function);

        if (this.calculators[statId].size() == 0) {
            this.calculators[statId] = null;
        }

        if (this.entity instanceof NPCEntity) {
            int i = 0;
            for (; i < Stats.NUM_STATS; i++) {
                if (!StatCalculator.equalsCals(this.calculators[i], NPC_CALCULATORS[i])) {
                    break;
                }
            }

            if (i >= Stats.NUM_STATS) {
                this.calculators = NPC_CALCULATORS;
            }
        }
    }

    /**
     * Удаляет функции.
     *
     * @param functions - функции к удалению.
     */
    private synchronized void removeStatFunctions(@NotNull StatFunction[] functions) {
        List<Stats> modifiedStats = Lists.newArrayList();
        for (StatFunction function : functions) {
            modifiedStats.add(function.getStat());
            this.removeStatFunction(function);
        }

        this.broadcastModifiedStats(modifiedStats);
    }

    /**
     * Удаляет функции, источником которых является #source.
     *
     * @param source - источник функции.
     */
    public final synchronized void removeStatsSource(@NotNull Object source) {
        List<Stats> modifiedStats = null;

        for (int i = 0, length = this.calculators.length; i < length; i++) {
            StatCalculator calculator = this.calculators[i];
            if (calculator != null) {
                if (modifiedStats != null) {
                    modifiedStats.addAll(calculator.removeSource(source));
                } else {
                    modifiedStats = calculator.removeSource(source);
                }

                if (this.calculators[i].size() == 0) {
                    this.calculators[i] = null;
                }
            }
        }

        if (this.entity instanceof NPCEntity) {
            int i = 0;
            for (; i < Stats.NUM_STATS; i++) {
                if (!StatCalculator.equalsCals(this.calculators[i], NPC_CALCULATORS[i])) {
                    break;
                }
            }

            if (i >= Stats.NUM_STATS) {
                this.calculators = NPC_CALCULATORS;
            }
        }

        if (modifiedStats != null) {
            this.broadcastModifiedStats(modifiedStats);
        }
    }

    @NotNull
    public final StatCalculator[] getCalculators() {
        return this.calculators;
    }

    private void broadcastModifiedStats(@NotNull List<Stats> stats) {
        if (stats.isEmpty()) {
            return;
        }

        for (Stats stat : stats) {
        }

        stats.stream().forEach((stat) -> {
            this.apply(stat);
        });
    }

    public void apply(@NotNull Stats stat) {
        if (stat == Stats.MOVEMENT_SPEED) {
        }
    }

    public final double calculate(@NotNull Stats stat, double baseValue, @Nullable BasicEntity target) {
        int statId = stat.ordinal();

        StatCalculator calculator = this.calculators[statId];

        if ((calculator == null) || (calculator.size() == 0)) {
            return baseValue;
        }

        StatContext context = new StatContext();
        context.value = baseValue;

        calculator.calculate(context);
        
        if(context.value < stat.getMinValue()) {
            context.value = stat.getMinValue();
        } else if(context.value > stat.getMaxValue()) {
            context.value = stat.getMaxValue();
        }

        return context.value;
    }

    /**
     * Получает количество физического урона (база+модификаторы) для
     * BasicEntity.
     *
     * @param target
     * @return
     */
    public double getAttackDamage(@Nullable BasicEntity target) {
        if (this.entity.getTemplate() == null) {
            return this.entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 2.0D);
        }
        return this.calculate(Stats.ATTACK_DAMAGE, this.entity.getTemplate().getBaseAttackDamage(), target);
    }

    /**
     * Получает количество физической брони (база+модификаторы) от BasicEntity.
     *
     * @param attacker
     * @return
     */
    public double getDefense(@Nullable BasicEntity attacker) {
        if (this.entity.getTemplate() == null) {
            return this.entity.getAttribute(Attribute.GENERIC_ARMOR, 2.0D);
        }
        return this.calculate(Stats.DEFENSE, this.entity.getTemplate().getBaseDefense(), attacker);
    }

    /**
     * Получает скорость бега (база+модификаторы).
     *
     * @return
     */
    public double getMovementSpeed() {
        if (this.entity.getTemplate() == null) {
            return this.entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.2D);
        }
        return this.calculate(Stats.MOVEMENT_SPEED, this.entity.getTemplate().getBaseMovementSpeed(), null);
    }
}
