package ru.civwars.building.validation;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import org.bukkit.ChunkSnapshot;
import ru.civwars.TaskMaster;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.block.SchematicBlock;
import ru.civwars.building.BuildingLayer;
import ru.civwars.thread.CivAsyncTask;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.ChunkPos;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class StructureValidatorTask extends CivAsyncTask {

    private final Schematic schematic;

    private final CivWorld world;
    private final BlockPos pos;

    private final Consumer<StructureValidatorResult> consumer;

    public StructureValidatorTask(@NotNull Schematic schematic, @NotNull CivWorld world, @NotNull BlockPos pos, @Nullable Consumer<StructureValidatorResult> consumer) {
        this.schematic = schematic;
        this.world = world;
        this.pos = pos;
        this.consumer = consumer;
    }

    public StructureValidatorTask(@NotNull Schematic schematic, @NotNull CivWorld world, @NotNull BlockPos pos) {
        this(schematic, world, pos, null);
    }

    public ScheduledFuture<?> execute() {
        return TaskMaster.scheduleGeneral(this, 0);
    }

    @Override
    public void run() {
        Map<ChunkPos, ChunkSnapshot> snapshots = this.getChunkSnapshots(this.world, this.pos.getX(), this.pos.getZ(), this.schematic.getSize().width, this.schematic.getSize().length);

        boolean isValid = true;
        Map<Integer, BuildingLayer> layers = Maps.newHashMap();
        int invalidY = -1;

        for (int y = this.pos.getY() - 1; y > 0; y--) {
            int totalBlocksCount = 0;
            int totalReinforcement = 0;

            for (int z = 0; z < this.schematic.getSize().length; z++) {
                for (int x = 0; x < this.schematic.getSize().width; x++) {
                    SchematicBlock sb = this.schematic.getBlock(x, 0, z);
                    if (sb.getBlockId() == 0) {
                        continue;
                    }

                    int absX = this.pos.getX() + x;
                    int absZ = this.pos.getZ() + z;

                    int blockId = BlockUtils.getBlockIDFromSnapshotMap(snapshots, absX, y, absZ);
                    totalBlocksCount++;
                    totalReinforcement += BlockUtils.getReinforcementValue(blockId);
                }
            }

            layers.put(y, new BuildingLayer(totalBlocksCount, totalReinforcement));

            if (isValid) {
                double percentValid = ((double) totalReinforcement) / ((double) totalBlocksCount);
                if (percentValid < BlockUtils.getReinforcementForLevel(this.pos.getY() - y - 1)) {
                    isValid = false;
                    invalidY = y;
                    continue;
                }
            }
        }

        if (this.consumer != null) {
            StructureValidatorResult result = new StructureValidatorResult(this.schematic, this.world, this.pos, isValid, layers, invalidY);
            TaskMaster.runTask(() -> {
                consumer.accept(result);
            });
        }
    }

}
