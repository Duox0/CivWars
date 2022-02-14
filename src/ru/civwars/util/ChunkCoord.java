package ru.civwars.util;

import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public class ChunkCoord {

    private final UUID worldId;
    private final int x;
    private final int z;

    public ChunkCoord(@NotNull UUID worldId, int x, int z) {
        this.worldId = worldId;
        this.x = x;
        this.z = z;
    }

    public ChunkCoord(@NotNull World world, int x, int z) {
        this(world.getUID(), x, z);
    }

    public ChunkCoord(@NotNull Block block) {
        this(block.getWorld(), block.getX() >> 4, block.getZ() >> 4);
    }

    /**
     * Получает идентификатор мира чанка.
     *
     * @return
     */
    @NotNull
    public final UUID getWorldId() {
        return this.worldId;
    }

    /**
     * Получает X позицию чанка.
     *
     * @return
     */
    public final int getX() {
        return this.x;
    }

    /**
     * Получает Z позицию чанка.
     *
     * @return
     */
    public final int getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.worldId.hashCode();
        hash = 19 * hash + (int) (this.x ^ this.x >>> 31);
        hash = 19 * hash + (int) (this.z ^ this.z >>> 31);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (!(other instanceof ChunkCoord)) {
            return false;
        }
        ChunkCoord coord = (ChunkCoord) other;

        if (!this.worldId.equals(coord.worldId) || this.x != coord.x || this.z != coord.z) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChunkCoord{world=" + this.worldId + ",x=" + this.x + ",z=" + this.z + "}";
    }

    public int manhattanDistance(@NotNull ChunkCoord coord) {
        return Math.abs(coord.x - this.x) + Math.abs(coord.z - this.z);
    }

}
