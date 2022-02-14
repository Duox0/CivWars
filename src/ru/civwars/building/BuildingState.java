package ru.civwars.building;

import ru.lib27.annotation.NotNull;

public enum BuildingState {
    NONE(0),
    COPY_BLOCKS(1),
    CLEAR_BLOCKS(2),
    BUILD_SCAFFOLDING(3),
    BUILD(4),
    COMPLETE(5),
    ACTIVE(9);

    private final int id;

    private BuildingState(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }

    @NotNull
    public static BuildingState getBuildingState(int id) {
        for (BuildingState state : BuildingState.values()) {
            if (state.id == id) {
                return state;
            }
        }
        return BuildingState.NONE;
    }

}
