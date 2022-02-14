package ru.civwars.building;

import ru.civwars.building.types.BuildingData;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;
import ru.civwars.CivWars;
import ru.civwars.schematic.Schematic;
import ru.civwars.util.BlockPos;
import ru.civwars.util.ChunkPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public class BuildingPlacer {

    private final CivWars civcraft = CivWars.get();

    private final BuildingData building;
    private final Schematic schematic;

    private final int[] size;

    public BuildingPlacer(@NotNull BuildingData building, @NotNull Schematic schematic) {
        this.building = building;

        this.schematic = schematic;
        this.size = new int[]{schematic.getSize().width, schematic.getSize().height, schematic.getSize().length};
    }

    public BuildError canPlaceHere(@NotNull CivWorld world, int x, int y, int z) {
        if (y >= 255) {
            return BuildError.TOO_HIGH;
        }

        if (y <= 7) {
            return BuildError.TOO_LOW;
        }

        if (y + this.schematic.getSize().height >= 255) {
            return BuildError.HEIGHT_LIMIT;
        }

        final BlockPos[] corners = new BlockPos[]{
            new BlockPos(x, y, z),
            new BlockPos(x + this.size[0] - 1, y, z),
            new BlockPos(x, y, z + this.size[2] - 1),
            new BlockPos(x + this.size[0] - 1, y, z + this.size[2] - 1),
            new BlockPos(x, y + this.size[1] - 1, z),
            new BlockPos(x + this.size[0] - 1, y + this.size[1] - 1, z),
            new BlockPos(x, y + this.size[1], z + this.size[2]),
            new BlockPos(x + this.size[0] - 1, y + this.size[1] - 1, z + this.size[2] - 1)
        };

        // check border
        for (int j = 0; j < 4; j++) {
            BlockPos corner = corners[j];
            BorderData border = Config.Border(world.getName());
            if (border != null) {
                if (!border.insideBorder(corner.getX(), corner.getZ(), Config.ShapeRound())) {
                    return BuildError.OUTSIDE_BORDER;
                }
            }
        }

        final ChunkPos[] cornerChunks = new ChunkPos[]{
            new ChunkPos(corners[0].getX() >> 4, corners[0].getZ() >> 4),
            new ChunkPos(corners[1].getX() >> 4, corners[1].getZ() >> 4),
            new ChunkPos(corners[2].getX() >> 4, corners[2].getZ() >> 4),
            new ChunkPos(corners[3].getX() >> 4, corners[3].getZ() >> 4)
        };

        int chunksSize = (cornerChunks[3].getX() - cornerChunks[0].getX() + 1) * (cornerChunks[3].getZ() - cornerChunks[0].getZ() + 1);
        int[][] chunks = new int[chunksSize][2];

        int i = 0;
        for (int zz = cornerChunks[0].getZ(); zz <= cornerChunks[3].getZ(); zz++) {
            for (int xx = cornerChunks[0].getX(); xx <= cornerChunks[3].getX(); xx++) {
                chunks[i][0] = xx;
                chunks[i][1] = zz;
                i++;
            }
        }

        // find buildigns at chunks
        for (int j = 0; j < chunks.length; j++) {
            Building findBuilding = world.getBuildingAtChunk(chunks[j][0], chunks[j][1]);
            if (findBuilding != null) {
                return BuildError.BUILDING_IN_CHUNK;
            }
        }

        return BuildError.OK;
    }

}
