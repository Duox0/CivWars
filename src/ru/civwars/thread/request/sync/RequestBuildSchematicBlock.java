package ru.civwars.thread.request.sync;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.civwars.schematic.block.SchematicBlock;
import ru.civwars.building.instance.Structure;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RequestBuildSchematicBlock extends RequestBuildBlock {

    protected final SchematicBlock schematicBlock;
    
    private final Structure building;

    public RequestBuildSchematicBlock(@NotNull World world, int x, int y, int z, @NotNull SchematicBlock schematicBlock, @Nullable Structure building) {
        super(world, x, y, z, 0, (byte) 0);
        this.schematicBlock = schematicBlock;
        this.building = building;
    }

    @Override
    public void update() {
        World world = Bukkit.getWorld(this.worldId);
        if (world == null) {
            return;
        }

        Block block = world.getBlockAt(this.x, this.y, this.z);
        this.schematicBlock.setBlock(block);
        
        if(this.building != null) {
            this.building.changeBuiltBlocksCount();
        }
    }
}
