package ru.civwars.schematic;

import ru.civwars.schematic.block.SchematicBlock;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import ru.civwars.util.BlockPos;
import ru.civwars.util.BlockUtils;
import ru.civwars.util.Size3i;
import ru.lib27.annotation.NotNull;

public class Schematic {

    private final String filepath;
    private final Size3i size;
    private final int yShift;
    private final SchematicBlock[][][] blocks;

    private final List<BlockPos> commandBlocks;
    private final List<BlockPos> attachableBlocks;

    private final List<BlockPos> scaffolding;

    public Schematic(@NotNull String filepath, int yShift, @NotNull SchematicBlock[][][] blocks) {
        this.filepath = filepath;
        this.yShift = yShift;
        this.blocks = blocks;

        int width = blocks.length;
        int height = blocks.length > 0 ? blocks[0].length : 0;
        int length = blocks[0].length > 0 ? blocks[0][0].length : 0;
        this.size = new Size3i(width, height, length);

        this.commandBlocks = Lists.newLinkedList();
        this.attachableBlocks = Lists.newLinkedList();

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    SchematicBlock sb = blocks[x][y][z];
                    if (sb.isCommand()) {
                        this.commandBlocks.add(new BlockPos(x, y, z));
                    } else if (BlockUtils.isAttachable(sb.getBlockId())) {
                        this.attachableBlocks.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        this.scaffolding = Lists.newLinkedList();

        for (int z = 0; z < length; z++) {
            for (int x = 0; x < width; x++) {
                this.scaffolding.add(new BlockPos(x, 0, z));
            }
        }

        for (int y = 1; y < height; y++) {
            this.scaffolding.add(new BlockPos(0, y, 0));
            this.scaffolding.add(new BlockPos(width - 1, y, 0));
            this.scaffolding.add(new BlockPos(0, y, length - 1));
            this.scaffolding.add(new BlockPos(width - 1, y, length - 1));
        }

        for (int x = 1; x < width; x++) {
            this.scaffolding.add(new BlockPos(x, height - 1, 0));
            this.scaffolding.add(new BlockPos(x, height - 1, length - 1));
        }

        for (int z = 1; z < width; z++) {
            this.scaffolding.add(new BlockPos(0, height - 1, z));
            this.scaffolding.add(new BlockPos(width - 1, height - 1, z));
        }
    }

    public Schematic(@NotNull String filepath, @NotNull SchematicBlock[][][] blocks) {
        this(filepath, 0, blocks);
    }

    /**
     *
     * @return путь до схематика.
     */
    @NotNull
    public String getFilepath() {
        return this.filepath;
    }
    
    /**
     * @return размер схематика.
     */
    @NotNull
    public final Size3i getSize() {
        return this.size;
    }

    /**
     * @return смещение по оси Y.
     */
    public int getYShift() {
        return this.yShift;
    }

    /**
     * @param ix
     * @param iy
     * @param iz
     * @return блок схематика по индексу.
     */
    @NotNull
    public SchematicBlock getBlock(int ix, int iy, int iz) {
        return this.blocks[ix][iy][iz];
    }

    @NotNull
    public Collection<BlockPos> getCommandBlocks() {
        return this.commandBlocks;
    }

    @NotNull
    public Collection<BlockPos> getAttachableBlocks() {
        return this.attachableBlocks;
    }

    @NotNull
    public Collection<BlockPos> getScaffolding() {
        return this.scaffolding;
    }
}
