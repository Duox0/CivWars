package ru.civwars.building.task;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.civwars.building.Buildable;
import ru.civwars.building.BuildingState;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class SimpleBuildBlock<T extends Buildable> extends BuildBlock<T> {

    private final int blockId;
    private final byte blockData;

    public SimpleBuildBlock(@NotNull UUID worldId, int x, int y, int z, int blockId, byte blockData, @NotNull T buildable, @NotNull BuildingState state) {
        super(worldId, x, y, z, buildable, state);
        this.blockId = blockId;
        this.blockData = blockData;
    }

    public SimpleBuildBlock(@NotNull World world, int x, int y, int z, int blockId, byte blockData, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getUID(), x, y, z, blockId, blockData, buildable, state);
    }

    public SimpleBuildBlock(@NotNull CivWorld world, int x, int y, int z, int blockId, byte blockData, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getId(), x, y, z, blockId, blockData, buildable, state);
    }
    
    @Override
    public int getBlockId() {
        return this.blockId;
    }
    
    @Override
    public byte getBlockData() {
        return this.blockData;
    }

    @Override
    protected void buildBlock(@NotNull CivWorld world, @NotNull Block block) {
        block.setTypeIdAndData(this.blockId, this.blockData, true);
    }
}
