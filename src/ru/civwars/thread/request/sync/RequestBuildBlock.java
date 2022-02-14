package ru.civwars.thread.request.sync;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RequestBuildBlock extends SyncRequest {

    protected final UUID worldId;
    protected final int x;
    protected final int y;
    protected final int z;

    protected final int blockId;
    protected final byte blockData;

    public RequestBuildBlock(@NotNull World world, int x, int y, int z, int blockId, byte blockData) {
        this.worldId = world.getUID();
        this.x = x;
        this.y = y;
        this.z = z;

        this.blockId = blockId;
        this.blockData = blockData;
    }

    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(this.worldId);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void update() {
        World world = Bukkit.getWorld(this.worldId);
        if (world == null) {
            return;
        }

        Block block = world.getBlockAt(this.x, this.y, this.z);
        block.setTypeIdAndData(this.blockId, this.blockData, true);
    }
}
