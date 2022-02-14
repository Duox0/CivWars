package ru.civwars.entity.template;

import ru.lib27.annotation.NotNull;

public class BasicEntityTemplate {

    /* Идентификатор шаблона. */
    private final int id;

    /* Имя шаблона. */
    private final String name;

    // Stats type (health, attack, defense... etc)
    private final double baseHealth;
    private final double baseDefense;
    private final double baseDefenseToughness;
    private final double baseAttackDamage;
    private final double baseAttackSpeed;
    private final double baseMovementSpeed;
    private final double baseFlyingSpeed;
    private final double baseKnockbackResistance;

    public BasicEntityTemplate(@NotNull Property property) {
        this.id = property.id;
        this.name = property.name;

        this.baseHealth = property.baseHealth;
        this.baseDefense = property.baseDefense;
        this.baseDefenseToughness = property.baseDefenseToughness;
        this.baseAttackDamage = property.baseAttackDamage;
        this.baseAttackSpeed = property.baseAttackSpeed;
        this.baseMovementSpeed = property.baseMovementSpeed;
        this.baseFlyingSpeed = property.baseFlyingSpeed;
        this.baseKnockbackResistance = property.baseKnockbackResistance;
    }

    /**
     * Получает идентификатор шаблона.
     *
     * @return идентификатор шаблона.
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Получает имя шаблона.
     *
     * @return имя шаблона.
     */
    @NotNull
    public final String getName() {
        return this.name;
    }

    public final double getBaseHealth() {
        return this.baseHealth;
    }

    public final double getBaseDefense() {
        return this.baseDefense;
    }

    public final double getBaseDefenseToughness() {
        return this.baseDefenseToughness;
    }

    public final double getBaseAttackDamage() {
        return this.baseAttackDamage;
    }

    public final double getBaseAttackSpeed() {
        return this.baseAttackSpeed;
    }

    public final double getBaseMovementSpeed() {
        return this.baseMovementSpeed;
    }

    public final double getBaseFlyingSpeed() {
        return this.baseFlyingSpeed;
    }

    public final double getBaseKnockbackResistance() {
        return this.baseKnockbackResistance;
    }

    public abstract static class Property {

        private final int id;
        private final String name;

        // Stats type (health, attack, defense... etc)
        private double baseHealth = 20.0D;
        private double baseDefense = 2.0D;
        private double baseDefenseToughness = 0.0D;
        private double baseAttackDamage = 4.0D;
        private double baseAttackSpeed = 1.0D;
        private double baseMovementSpeed = 0.25D;
        private double baseFlyingSpeed = 0.25D;
        private double baseKnockbackResistance = 0.0D;

        public Property(int id, @NotNull String name) {
            this.id = id;
            this.name = name;
        }

        public Property baseHealth(double baseHealth) {
            this.baseHealth = baseHealth;
            return this;
        }

        public Property baseDefense(double baseDefense) {
            this.baseDefense = baseDefense;
            return this;
        }

        public Property baseDefenseToughness(double baseDefenseToughness) {
            this.baseDefenseToughness = baseDefenseToughness;
            return this;
        }

        public Property baseAttackDamage(double baseAttackDamage) {
            this.baseAttackDamage = baseAttackDamage;
            return this;
        }

        public Property baseAttackSpeed(double baseAttackSpeed) {
            this.baseAttackSpeed = baseAttackSpeed;
            return this;
        }

        public Property baseMovementSpeed(double baseMovementSpeed) {
            this.baseMovementSpeed = baseMovementSpeed;
            return this;
        }

        public Property baseFlyingSpeed(double baseFlyingSpeed) {
            this.baseFlyingSpeed = baseFlyingSpeed;
            return this;
        }

        public Property baseKnockbackResistance(double baseKnockbackResistance) {
            this.baseKnockbackResistance = baseKnockbackResistance;
            return this;
        }

    }

}
