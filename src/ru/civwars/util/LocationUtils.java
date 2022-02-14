package ru.civwars.util;

import org.bukkit.Location;
import ru.lib27.annotation.NotNull;

public class LocationUtils {

    private LocationUtils() {
    }

    @NotNull
    public static BlockPos repositionCorner(@NotNull BlockPos pos, @NotNull EnumFacing facing, int width, int length) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        switch (facing) {
            case EAST:
                chunkX++;
                break;
            case SOUTH:
                chunkX -= ((width - 1) >> 4);
                chunkZ++;
                break;
            case WEST:
                chunkX -= 1;
                chunkX -= ((width - 1) >> 4);
                chunkZ -= ((length - 1) >> 4);
                break;
            case NORTH:
                chunkZ -= 1;
                chunkZ -= ((length - 1) >> 4);
                break;
        }

        return new BlockPos(chunkX << 4, pos.getY(), chunkZ << 4);
    }

    @NotNull
    public static Location repositionCorner(@NotNull Location location, @NotNull EnumFacing facing, int width, int length) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        switch (facing) {
            case EAST:
                chunkX++;
                break;
            case SOUTH:
                chunkX -= ((width - 1) >> 4);
                chunkZ++;
                break;
            case WEST:
                chunkX -= 1;
                chunkX -= ((width - 1) >> 4);
                chunkZ -= ((length - 1) >> 4);
                break;
            case NORTH:
                chunkZ -= 1;
                chunkZ -= ((length - 1) >> 4);
                break;
        }

        Location corner = new Location(location.getWorld(), chunkX << 4, location.getBlockY(), chunkZ << 4);

        return corner;
    }

}
