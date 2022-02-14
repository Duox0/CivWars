package ru.civwars.petitions;

import java.util.UUID;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.civwars.building.types.TownHallData;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.util.EnumFacing;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class PetitionTown extends Petition {

    private final String name;
    private final TownHallData type;
    private final UUID worldId;
    private final BlockPos pos;
    private final EnumFacing direction;
    private final String schematic;

    public PetitionTown(@NotNull UUID id, @NotNull String name, @NotNull TownHallData type, @NotNull CivWorld world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull Schematic schematic) {
        super(id);
        this.name = name;
        this.type = type;
        this.worldId = world.getId();
        this.pos = pos;
        this.direction = direction;
        this.schematic = schematic.getFilepath();
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final TownHallData getType() {
        return this.type;
    }

    @Nullable
    public final CivWorld getWorld() {
        return WorldManager.getWorld(this.worldId);
    }

    @NotNull
    public final BlockPos getPos() {
        return this.pos;
    }

    @NotNull
    public final EnumFacing getDirection() {
        return this.direction;
    }

    @Nullable
    public final Schematic getSchematic() {
        return SchematicManager.getSchematic(this.schematic, false);
    }
}
