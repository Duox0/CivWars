package ru.civwars.thread.request.sync;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import ru.lib27.annotation.NotNull;

public class RequestBuildSign extends RequestBuildBlock {

    private final String[] lines;

    public RequestBuildSign(@NotNull World world, int x, int y, int z, int blockId, byte blockData, String... lines) {
        super(world, x, y, z, blockId, blockData);
        this.lines = lines;
    }

    @Override
    public void update() {
        World world = Bukkit.getWorld(this.worldId);
        if (world == null) {
            return;
        }

        Block block = world.getBlockAt(this.x, this.y, this.z);
        block.setTypeIdAndData(this.blockId, this.blockData, true);

        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            for(int length = this.lines.length, i = 0;i < 4 && i < length;i++) {
                sign.setLine(i, this.lines[i]);
            }
            sign.update();
        }
    }
}
