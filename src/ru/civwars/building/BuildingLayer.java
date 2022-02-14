package ru.civwars.building;

public class BuildingLayer {

    private final int totalBlocksCount;
    private int reinforcement;

    public BuildingLayer(int totalBlocksCount, int reinforcement) {
        this.totalBlocksCount = totalBlocksCount;
        this.reinforcement = reinforcement;
    }

    public BuildingLayer(int totalBlocksCount) {
        this(totalBlocksCount, 0);
    }

    public int getTotalBlocksCount() {
        return this.totalBlocksCount;
    }

    public void setReinforcement(int reinforcement) {
        this.reinforcement = reinforcement;
    }

    public int getReinforcement() {
        return this.reinforcement;
    }

    public void changeReinforcement(int amount) {
        if (amount == 0) {
            return;
        }

        if (this.reinforcement + amount < 0) {
            amount = -this.reinforcement;
        }

        this.reinforcement += amount;
    }

    public double getBlocksPercent() {
        if (this.totalBlocksCount <= 0) {
            return 0.0D;
        }
        return ((double) (this.reinforcement) / (double) this.totalBlocksCount);
    }

}
