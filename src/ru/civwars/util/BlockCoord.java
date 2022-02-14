package ru.civwars.util;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;

public class BlockCoord {

    private final UUID worldId;
    private final int x;
    private final int y;
    private final int z;

    public BlockCoord(@NotNull UUID worldId, int x, int y, int z) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockCoord(@NotNull World world, int x, int y, int z) {
        this(world.getUID(), x, y, z);
    }

    public BlockCoord(@NotNull Location loc) {
        this(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public BlockCoord(@NotNull Block block) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }
    
    /**
     * Получает идентификатор мира блока.
     *
     * @return
     */
    @NotNull
    public final UUID getWorldId() {
        return this.worldId;
    }

    /**
     * Получает X позицию блока.
     *
     * @return
     */
    public final int getX() {
        return this.x;
    }

    /**
     * Получает Y позицию блока.
     *
     * @return
     */
    public final int getY() {
        return this.y;
    }

    /**
     * Получает Z позицию блока.
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
        } else if (!(other instanceof BlockCoord)) {
            return false;
        }
        BlockCoord pos = (BlockCoord) other;

        if (!this.worldId.equals(pos.worldId) || this.x != pos.x || this.z != pos.z || this.y != pos.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BlockPos{world=" + this.worldId + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + "}";
    }
}
