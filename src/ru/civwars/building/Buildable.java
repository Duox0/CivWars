package ru.civwars.building;

import ru.civwars.building.types.BuildingData;
import ru.civwars.building.task.SimpleBuildBlock;
import ru.civwars.building.task.BuildBlockSchematic;
import ru.civwars.building.task.BuildBlock;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.civwars.CivLogger;
import ru.civwars.TaskMaster;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.schematic.block.SchematicBlock;
import ru.civwars.schematic.block.command.SchematicBlockCommand;
import ru.civwars.thread.CivAsyncTask;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.ChunkPos;
import ru.civwars.util.Size3i;
import ru.civwars.world.CivWorld;
import ru.lib27.annotation.NotNull;

public abstract class Buildable extends Building {

    private static final boolean DEBUG = true;

    private static final Map<BuildingState, BuildingState> STATES = Maps.newHashMap();

    static {
        STATES.put(BuildingState.COPY_BLOCKS, BuildingState.CLEAR_BLOCKS);
        STATES.put(BuildingState.CLEAR_BLOCKS, BuildingState.BUILD_SCAFFOLDING);
        STATES.put(BuildingState.BUILD_SCAFFOLDING, BuildingState.BUILD);
        STATES.put(BuildingState.BUILD, BuildingState.CLEAR_BLOCKS);
        STATES.put(BuildingState.BUILD, BuildingState.COMPLETE);
        STATES.put(BuildingState.COMPLETE, BuildingState.ACTIVE);
    }

    /* Текущее состояние постройки (строится, активно). */
    private BuildingState currentState = BuildingState.COPY_BLOCKS;

    private ScheduledFuture<?> buildTask = null;

    /* Количество построенных блоков. */
    private int builtBlocksCount = 0;

    public Buildable(@NotNull UUID id, @NotNull BuildingData data, @NotNull CivWorld world) {
        super(id, data, world);
    }

    /**
     * Устанавливает текущее состояние постройки.
     *
     * @param newState - новое состояние постройки.
     */
    public final void setState(@NotNull BuildingState newState) {
        this.currentState = newState;
    }

    /**
     *
     * @return текущее состояние постройки.
     */
    @NotNull
    public final BuildingState getState() {
        return this.currentState;
    }

    /**
     * Изменяет текущее состояние постройки.
     *
     * @param newState - новое состояние постройки.
     */
    public void changeState(@NotNull BuildingState newState) {
        this.setState(newState);
    }

    /**
     * Является ли завершенной.
     *
     * @return {@code true}, если завершена. Иначе {@code false}.
     */
    public boolean isCompleted() {
        return this.currentState == BuildingState.ACTIVE;
    }

    /**
     * Устанавливает количество построенных блоков.
     *
     * @param newBuiltBlocksCount - новое количество блоков.
     */
    public void setBuiltBlocksCount(int newBuiltBlocksCount) {
        this.builtBlocksCount = newBuiltBlocksCount;
    }

    /**
     * @return количество построенных блоков.
     */
    public int getBuiltBlocksCount() {
        return this.builtBlocksCount;
    }

    /**
     * Изменяет количество построенных блоков.
     */
    public void changeBuiltBlocksCount() {
        this.builtBlocksCount += 1;
    }

    /**
     * Запускает задачу строительства.
     */
    public final void startBuildTask() {
        if (DEBUG) {
            CivLogger.log(Level.INFO, "Start build task for state {0}", this.currentState.name());
        }

        if (this.buildTask != null) {
            return;
        }

        switch (this.currentState) {
            case COPY_BLOCKS:
                this.buildTask = TaskMaster.scheduleGeneral(new CopyBlocksTask(), 0);
                break;
            case CLEAR_BLOCKS:
                this.buildTask = TaskMaster.scheduleGeneralAtFixedRate(new ClearBlocksTask(), 0, 1000L);
                break;
            case BUILD_SCAFFOLDING:
                this.buildTask = TaskMaster.scheduleGeneralAtFixedRate(new BuildScaffoldingTask(), 0, 1000L);
                break;
            case BUILD:
                this.buildTask = TaskMaster.scheduleGeneralAtFixedRate(new BuildTask(), 0, 1000L);
                break;
            case COMPLETE:
                this.buildTask = TaskMaster.scheduleGeneralAtFixedRate(new CompleteTask(), 0, 1000L);
                break;
            default:
                CivLogger.log(Level.WARNING, "Unknown build task for state {0}", this.currentState.name());
                break;
        }
    }

    /**
     * Останавливает задачу строительства.
     */
    public final void stopBuildTask() {
        if (DEBUG) {
            CivLogger.log(Level.INFO, "Stop build task for state {0}", this.currentState.name());
        }

        if (this.buildTask == null) {
            return;
        }

        this.buildTask.cancel(true);
        this.buildTask = null;
    }

    /**
     * Вызывается при завершении задачи строительства.
     *
     * @param state - завершенная задача.
     */
    public void completeBuildTask(@NotNull BuildingState state) {
        if (DEBUG) {
            CivLogger.log(Level.INFO, "Complete build task for state {0}", this.currentState.name());
        }

        this.stopBuildTask();

        BuildingState nextState = STATES.getOrDefault(state, BuildingState.NONE);
        if (this.currentState == nextState) {
            return;
        }

        switch (nextState) {
            case CLEAR_BLOCKS:
            case BUILD:
                this.builtBlocksCount = 0;
                break;
        }

        this.changeState(nextState);

        if (nextState == BuildingState.ACTIVE) {
            this.onComplete();
        } else {
            this.startBuildTask();
        }
    }

    public boolean canBuildBlock(@NotNull CivWorld world, @NotNull BuildBlock block) {
        return true;
    }

    public void onBuildBlock(@NotNull CivWorld world, @NotNull BuildBlock block) {
        if (block.getState() != this.getState()) {
            return;
        }

        if (!(block.getState() == BuildingState.CLEAR_BLOCKS || block.getState() == BuildingState.BUILD)) {
            return;
        }

        if (block.getState() == BuildingState.CLEAR_BLOCKS) {
            this.changeBuiltBlocksCount();
            this.updateProgress(block.getState());
        } else if (block.getState() == BuildingState.BUILD) {
            this.changeBuiltBlocksCount();
            this.updateProgress(block.getState());

            if (block.getBlockId() != 0) {
                BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
                BuildingBlock bb = new BuildingBlock(pos, this);
                this.addBlock(bb);
                world.addBuildingBlock(bb);
            }
        }
    }

    public void onComplete() {
        CivLogger.info("onComplete");
        if(this.getSchematicPath() == null) {
            return;
        }
        
        Schematic schematic = SchematicManager.getSchematic(this.getSchematicPath());
        if (schematic == null) {
            return;
        }

        World world = this.world.getWorld();

        for (BlockPos pos : schematic.getAttachableBlocks()) {
            SchematicBlock sb = schematic.getBlock(pos.getX(), pos.getY(), pos.getZ());
            Block block = world.getBlockAt(this.getX() + pos.getX(), this.getY() + pos.getY(), this.getZ() + pos.getZ());
            sb.setBlock(block);
        }

        for (BlockPos pos : schematic.getCommandBlocks()) {
            SchematicBlockCommand sb = (SchematicBlockCommand) schematic.getBlock(pos.getX(), pos.getY(), pos.getZ());
            Block block = world.getBlockAt(this.getX() + pos.getX(), this.getY() + pos.getY(), this.getZ() + pos.getZ());
            sb.onPostBuild(this, block);
        }
    }

    public void updateProgress(@NotNull BuildingState state) {
    }

    abstract class BaseBuildTask extends CivAsyncTask {

        private final BuildingState state;

        protected final CivWorld world;
        protected final BlockPos pos;
        protected final Schematic schematic;

        protected final Size3i size;
        protected final int totalBlocksCount;

        private final Queue<BuildBlock> blocks = Lists.newLinkedList();
        protected int builtBlocksCount;

        private boolean running = false;
        private boolean isCompleted = false;

        public BaseBuildTask(@NotNull BuildingState state) {
            if (DEBUG) {
                CivLogger.log(Level.INFO, "Initializing new build task: {0}", this.getClass().getSimpleName());
            }
            this.state = state;

            this.world = this.getBuildable().getWorld();
            this.pos = this.getBuildable().getCoord();
            this.schematic = SchematicManager.getSchematic(this.getBuildable().getSchematicPath());

            this.size = this.getBuildable().getSize() != null ? new Size3i(this.getBuildable().getSize()) : null;

            this.totalBlocksCount = this.getBuildable().getTotalBlocksCount();
            this.builtBlocksCount = this.getBuildable().getBuiltBlocksCount();
        }

        public final Buildable getBuildable() {
            return Buildable.this;
        }

        public final BuildingState getState() {
            return this.state;
        }

        @Override
        public final void run() {
            if (this.isCompleted) {
                return;
            }

            if (!this.running) {
                if (DEBUG) {
                    CivLogger.log(Level.INFO, "Start build task: {0}", this.getClass().getSimpleName());
                }
                this.running = true;
            }

            if (this.schematic == null || this.size == null) {
                this.complete();
                return;
            }

            this.update();

            if (this.canComplete()) {
                this.complete();
            }

            if (!this.blocks.isEmpty()) {
                this.buildBlocks(this.blocks);
                this.blocks.clear();
            }
        }

        protected abstract boolean canComplete();

        private void complete() {
            if (DEBUG) {
                CivLogger.log(Level.INFO, "Complete build task: {0}", this.getClass().getSimpleName());
            }

            this.isCompleted = true;
            this.blocks.add(new BuildBlock(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.getBuildable(), this.getState()) {
                @Override
                protected void buildBlock(@NotNull CivWorld world, @NotNull Block block) {
                }

                @Override
                protected void onPostBuild(@NotNull CivWorld world) {
                    this.getBuildable().completeBuildTask(this.getState());
                }
            });
        }

        public abstract void update();

        public final void buildBlock(int x, int y, int z, int blockId, byte blockData) {
            this.blocks.add(new SimpleBuildBlock(this.world, this.pos.getX() + x, this.pos.getY() + y, this.pos.getZ() + z, blockId, blockData, this.getBuildable(), this.getState()));
        }

        public final void buildBlock(int x, int y, int z, @NotNull SchematicBlock sb, boolean build) {
            if (build) {
                this.blocks.add(new BuildBlockSchematic(this.world, this.pos.getX() + x, this.pos.getY() + y, this.pos.getZ() + z, sb, this.getBuildable(), this.getState()));
            } else {
                this.blocks.add(new BuildBlockSchematic(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), sb, this.getBuildable(), this.getState()) {
                    @Override
                    protected void buildBlock(@NotNull CivWorld world, @NotNull Block block) {
                    }
                });
            }
        }
    }

    class CopyBlocksTask extends BaseBuildTask {

        private final String schematichUndoPath;

        private boolean finished = false;

        public CopyBlocksTask() {
            super(BuildingState.COPY_BLOCKS);
            this.schematichUndoPath = this.getBuildable().getSchematicUndoPath();
        }

        @Override
        protected boolean canComplete() {
            return this.finished;
        }

        @Override
        public void update() {
            this.finished = true;
            if (StringUtils.isBlank(this.schematichUndoPath)) {
                return;
            }

            FileWriter writer = null;
            try {
                File file = new File(this.schematichUndoPath);
                if (!file.exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        return;
                    }
                } else if (file.isFile()) {
                    file.delete();
                }

                file.createNewFile();

                writer = new FileWriter(file);

                writer.write(this.size.width + ";" + this.size.height + ";" + this.size.length + "\n");

                Map<ChunkPos, ChunkSnapshot> snapshots = this.getChunkSnapshots(this.world, this.pos.getX(), this.pos.getZ(), this.size.width, this.size.length);
                for (int y = 0; y < this.size.height; y++) {
                    for (int z = 0; z < this.size.length; z++) {
                        for (int x = 0; x < this.size.width; x++) {
                            int blockX = this.pos.getX() + x;
                            int blockZ = this.pos.getZ() + z;
                            int blockChunkX = (blockX % 16) + (blockX < 0 ? 16 : 0);
                            int blockChunkZ = (blockZ % 16) + (blockZ < 0 ? 16 : 0);

                            ChunkPos pos = new ChunkPos(blockX >> 4, blockZ >> 4);

                            String str = x + ":" + y + ":" + z + ",";

                            ChunkSnapshot snapshot = snapshots.get(pos);
                            if (snapshot != null) {
                                int blockId = snapshot.getBlockTypeId(blockChunkX, y, blockChunkZ);
                                if (!BlockUtils.isAttachable(blockId) && BlockUtils.isAllowedBlockForUndo(blockId)) {
                                    str += blockId + ":" + snapshot.getBlockData(blockChunkX, y, blockChunkZ);
                                } else {
                                    str += "0:0";
                                }
                            } else {
                                str += "0:0";
                            }

                            writer.write(str + "\n");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    class ClearBlocksTask extends BaseBuildTask {

        private static final int BLOCKS_PER_TICK = 8192;

        public ClearBlocksTask() {
            super(BuildingState.CLEAR_BLOCKS);
        }

        @Override
        protected boolean canComplete() {
            return (this.builtBlocksCount >= this.totalBlocksCount);
        }

        @Override
        public void update() {
            int blocksToBuild = Math.max(0, Math.min(BLOCKS_PER_TICK, this.totalBlocksCount - this.builtBlocksCount));

            for (int j = 0; j < blocksToBuild; j++) {
                int y = (this.builtBlocksCount / (this.size.width * this.size.length));
                int z = (this.builtBlocksCount / this.size.width) % this.size.length;
                int x = this.builtBlocksCount % this.size.width;

                this.buildBlock(x, y, z, 0, (byte) 0);
                this.builtBlocksCount += 1;
            }
        }
    }

    class BuildScaffoldingTask extends BaseBuildTask {

        private static final int BLOCKS_PER_TICK = 4096;

        private final List<BlockPos> scaffoldingBlocks = Lists.newLinkedList();
        private int scaffoldingIndex = 0;

        public BuildScaffoldingTask() {
            super(BuildingState.BUILD_SCAFFOLDING);
            for (int z = 0; z < this.size.length; z++) {
                for (int x = 0; x < this.size.width; x++) {
                    this.scaffoldingBlocks.add(new BlockPos(x, 0, z));
                }
            }

            for (int y = 1; y < this.size.height; y++) {
                this.scaffoldingBlocks.add(new BlockPos(0, y, 0));
                this.scaffoldingBlocks.add(new BlockPos(1, y, 0));
                this.scaffoldingBlocks.add(new BlockPos(0, y, 1));
                this.scaffoldingBlocks.add(new BlockPos(1, y, 1));
            }

            for (int x = 1; x < this.size.width; x++) {
                this.scaffoldingBlocks.add(new BlockPos(x, this.size.height - 1, 0));
                this.scaffoldingBlocks.add(new BlockPos(x, this.size.height - 1, this.size.length - 1));
            }

            for (int z = 1; z < this.size.length; z++) {
                this.scaffoldingBlocks.add(new BlockPos(0, this.size.height - 1, z));
                this.scaffoldingBlocks.add(new BlockPos(this.size.width - 1, this.size.height - 1, z));
            }
        }

        @Override
        protected boolean canComplete() {
            return (this.scaffoldingIndex >= this.scaffoldingBlocks.size());
        }

        @Override
        public void update() {
            int blocksToBuild = Math.max(0, Math.min(BLOCKS_PER_TICK, this.scaffoldingBlocks.size() - this.scaffoldingIndex));

            for (int i = 0; i < blocksToBuild; i++) {
                BlockPos pos = this.scaffoldingBlocks.get(this.scaffoldingIndex);
                this.buildBlock(pos.getX(), pos.getY(), pos.getZ(), 7, (byte) 0);
                this.scaffoldingIndex++;
            }
        }
    }

    class BuildTask extends BaseBuildTask {

        private static final int BLOCKS_PER_TICK = 8192;

        public BuildTask() {
            super(BuildingState.BUILD);
            this.builtBlocksCount = this.getBuildable().getBuiltBlocksCount();
        }

        @Override
        protected boolean canComplete() {
            return (this.schematic == null || this.builtBlocksCount >= this.totalBlocksCount);
        }

        @Override
        public void update() {
            if (this.schematic == null) {
                return;
            }

            int blocksToBuild = Math.max(0, Math.min(BLOCKS_PER_TICK, this.totalBlocksCount - this.builtBlocksCount));

            for (int j = 0; j < blocksToBuild; j++) {
                int y = (this.builtBlocksCount / (this.size.width * this.size.length));
                int z = (this.builtBlocksCount / this.size.width) % this.size.length;
                int x = this.builtBlocksCount % this.size.width;

                SchematicBlock sb = this.schematic.getBlock(x, y, z);
                if (!sb.isCommand()) {
                    this.buildBlock(x, y, z, sb, !BlockUtils.isAttachable(sb.getBlockId()));
                } else {
                    this.buildBlock(x, y, z, sb, false);
                }
                this.builtBlocksCount += 1;
            }
        }
    }

    class CompleteTask extends BaseBuildTask {

        public CompleteTask() {
            super(BuildingState.COMPLETE);
        }

        @Override
        protected boolean canComplete() {
            return true;
        }

        @Override
        public void update() {

        }
    }
}
