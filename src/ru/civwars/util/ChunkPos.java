package ru.civwars.util;

import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public class ChunkPos {

    private final int x;
    private final int z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPos(@NotNull Block block) {
        this(block.getX() >> 4, block.getZ() >> 4);
    }

    /**
     * @return координату по оси X.
     */
    public final int getX() {
        return this.x;
    }

    /**
     * @return координату по оси Z.
     */
    public final int getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        } else if (!(other instanceof ChunkPos)) {
            return false;
        }
        ChunkPos coord = (ChunkPos) other;

        if (this.x != coord.x || this.z != coord.z) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ChunkPos{x=" + this.x + ",z=" + this.z + "}";
    }
}
