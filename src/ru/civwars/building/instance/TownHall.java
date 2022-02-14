package ru.civwars.building.instance;

import java.util.UUID;
import ru.civwars.building.types.TownHallData;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class TownHall extends Structure {

    public TownHall(@NotNull UUID id, @NotNull TownHallData data, @NotNull CivWorld world) {
        super(id, data, world);
    }
}
