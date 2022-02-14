package ru.civwars.building.task;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.civwars.building.Buildable;
import ru.civwars.building.BuildingState;
import ru.civwars.schematic.block.SchematicBlock;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class BuildBlockSchematic<T extends Buildable> extends BuildBlock<T> {

    private final SchematicBlock schematicBlock;

    public BuildBlockSchematic(@NotNull UUID worldId, int x, int y, int z, @NotNull SchematicBlock schematicBlock, @NotNull T buildable, @NotNull BuildingState state) {
        super(worldId, x, y, z, buildable, state);
        this.schematicBlock = schematicBlock;
    }

    public BuildBlockSchematic(@NotNull World world, int x, int y, int z, @NotNull SchematicBlock schematicBlock, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getUID(), x, y, z, schematicBlock, buildable, state);
    }

    public BuildBlockSchematic(@NotNull CivWorld world, int x, int y, int z, @NotNull SchematicBlock schematicBlock, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getId(), x, y, z, schematicBlock, buildable, state);
    }
    
    @Override
    public int getBlockId() {
        return this.schematicBlock.getBlockId();
    }
    
    @Override
    public byte getBlockData() {
        return this.schematicBlock.getBlockData();
    }

    @Override
    protected void buildBlock(@NotNull CivWorld world, @NotNull Block block) {
        this.schematicBlock.setBlock(block);
    }
}
