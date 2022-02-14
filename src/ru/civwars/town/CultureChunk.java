package ru.civwars.town;

import java.lang.ref.WeakReference;
import java.util.UUID;
import ru.civwars.util.ChunkCoord;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CultureChunk {

    private final ChunkCoord coord;
    private Town town;

    private int distance = 0;

    private WeakReference<CivWorld> world = null;

    public CultureChunk(@NotNull ChunkCoord coord, @NotNull Town town) {
        this.coord = coord;
        this.town = town;
        this.world = new WeakReference<>(null);
    }

    @NotNull
    public ChunkCoord getCoord() {
        return this.coord;
    }
    
    @NotNull
    public final UUID getWorldId() {
        return this.coord.getWorldId();
    }

    @Nullable
    public final CivWorld getWorld() {
        CivWorld world = this.world.get();
        if (world == null) {
            world = WorldManager.getWorld(this.coord.getWorldId());
            this.world = new WeakReference<>(world);
        }
        return world;
    }

    public final int getX() {
        return this.coord.getX();
    }

    public final int getZ() {
        return this.coord.getZ();
    }

    public void setTown(@NotNull Town newTown) {
        this.town = newTown;
    }

    @NotNull
    public Town getTown() {
        return this.town;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getPower() {
        if(this.distance == 0) {
            return Integer.MAX_VALUE;
        }
        return Integer.MAX_VALUE / this.distance;
    }
}
