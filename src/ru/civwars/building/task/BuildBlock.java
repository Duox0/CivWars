package ru.civwars.building.task;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.civwars.CivWars;
import ru.civwars.building.Buildable;
import ru.civwars.building.BuildingState;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public abstract class BuildBlock<T extends Buildable> {
    
    private final UUID worldId;
    private final int x;
    private final int y;
    private final int z;
    
    private final T buildable;
    private final BuildingState state;
    
    public BuildBlock(@NotNull UUID worldId, int x, int y, int z, @NotNull T buildable, @NotNull BuildingState state) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.buildable = buildable;
        this.state = state;
    }
    
    public BuildBlock(@NotNull World world, int x, int y, int z, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getUID(), x, y, z, buildable, state);
    }
    
    public BuildBlock(@NotNull CivWorld world, int x, int y, int z, @NotNull T buildable, @NotNull BuildingState state) {
        this(world.getId(), x, y, z, buildable, state);
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getBlockId() {
        return 0;
    }
    
    public byte getBlockData() {
        return 0;
    }
    
    @NotNull
    public T getBuildable() {
        return this.buildable;
    }
    
    @NotNull
    public BuildingState getState() {
        return this.state;
    }
    
    public final void buildBlock() {
        CivWorld world = WorldManager.getWorld(this.worldId);
        if (world == null) {
            return;
        }
        
        if (!this.buildable.canBuildBlock(world, this)) {
            return;
        }
        
        Block block = world.getWorld().getBlockAt(this.x, this.y, this.z);
        this.buildBlock(world, block);
        this.onPostBuild(world);
    }
    
    protected abstract void buildBlock(@NotNull CivWorld world, @NotNull Block block);
    
    protected void onPostBuild(@NotNull CivWorld world) {
        this.buildable.onBuildBlock(world, this);
    }
}
