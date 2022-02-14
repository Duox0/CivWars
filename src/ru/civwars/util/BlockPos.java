package ru.civwars.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public class BlockPos {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(@NotNull BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos(@NotNull Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public BlockPos(@NotNull Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    /**
     * @return X позицию блока.
     */
    public final int getX() {
        return this.x;
    }

    /**
     * @return Y позицию блока.
     */
    public final int getY() {
        return this.y;
    }

    /**
     * @return Z позицию блока.
     */
    public final int getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (this.x ^ this.x >>> 31);
        hash = 19 * hash + (int) (this.y ^ this.y >>> 31);
        hash = 19 * hash + (int) (this.z ^ this.z >>> 31);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (!(other instanceof BlockPos)) {
            return false;
        }
        BlockPos pos = (BlockPos) other;

        if (this.x != pos.x || this.z != pos.z || this.y != pos.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BlockPos{x=" + this.x + ",y=" + this.y + ",z=" + this.z + "}";
    }

    @NotNull
    public BlockPos add(@NotNull BlockPos coord) {
        return new BlockPos(this.x + coord.x, this.y + coord.y, this.z + coord.z);
    }

    @NotNull
    public BlockPos add(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }
}
