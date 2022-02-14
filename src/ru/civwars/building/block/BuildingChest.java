package ru.civwars.building.block;

import ru.civwars.building.Building;
import ru.civwars.util.BlockPos;
import ru.lib27.annotation.NotNull;

public class BuildingChest extends BuildingBlock {

    private final int chestId;

    public BuildingChest(int chestId, @NotNull BlockPos position, @NotNull Building building) {
        super(position, building);
        this.chestId = chestId;
    }

    public int getChestId() {
        return this.chestId;
    }

    @Override
    public String toString() {
        return "BuildingChest{chest_id=" + this.chestId + ",position=" + this.position.toString() + ",buildingId=" + this.building.getObjectId() + "}";
    }

}
