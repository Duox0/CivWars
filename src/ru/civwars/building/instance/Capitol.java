package ru.civwars.building.instance;

import java.util.UUID;
import ru.civwars.building.types.CapitolData;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class Capitol extends TownHall {

    public Capitol(@NotNull UUID id, @NotNull CapitolData data, @NotNull CivWorld world) {
        super(id, data, world);
    }

}
