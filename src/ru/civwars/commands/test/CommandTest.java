package ru.civwars.commands.test;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import ru.civwars.CivLogger;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.task.CultureProcessTask;
import ru.civwars.util.BlockPos;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class CommandTest extends BasicCommand {

    public CommandTest(@NotNull CivWars plugin) {
        super(plugin, "test");
        this.setUsage("/test");
        this.setDescription("Test commands");
        this.setPermission("civcraft.commands.test");

        this.registerSubCommand(new CommandTown(plugin));
    }

    @CommandHandler
    public void handle(@NotNull AbstractCommandSender sender, Integer i) {
        sender.sendRawMessage("Is test command");
        
        long startTime = System.currentTimeMillis();

        CivWorld world = WorldManager.getWorld("world");
        World bukkitWorld = world.getWorld();

        for (int j = 0; j < i; j++) {
            List<BlockPos> chestsSrc = Lists.newArrayList(
                    new BlockPos(8 + (j * 16), 100, 8),
                    new BlockPos(8 + (j * 16), 100, 9)
            );
            List<BlockPos> chestsDest = Lists.newArrayList(
                    new BlockPos(9 + (j * 16), 100, 8),
                    new BlockPos(9 + (j * 16), 100, 9)
            );

            List<Inventory> invSrc = Lists.newArrayList();
            List<Inventory> invDest = Lists.newArrayList();

            for (BlockPos src : chestsSrc) {
                /*Chunk chunk = bukkitWorld.getChunkAt(src.getX() >> 4, src.getZ() >> 4);
                if (!chunk.isLoaded()) {
                    if (!chunk.load()) {
                        System.out.println("Cant load chunk");
                        continue;
                    }
                }*/

                Block b = bukkitWorld.getBlockAt(src.getX(), src.getY(), src.getZ());
                if (!(b.getState() instanceof Chest)) {
                    b.setTypeIdAndData(54, (byte) 0, true);
                    b.getState().update();
                }
                invSrc.add(((Chest) b.getState()).getBlockInventory());
            }

            for (BlockPos dest : chestsDest) {
                Block b = bukkitWorld.getBlockAt(dest.getX(), dest.getY(), dest.getZ());
                if (!(b.getState() instanceof Chest)) {
                    b.setTypeIdAndData(54, (byte) 0, true);
                    b.getState().update();
                }
                invDest.add(((Chest) b.getState()).getBlockInventory());
            }
        }

        CivLogger.info("Elapsed time: {0}", System.currentTimeMillis() - startTime);

        //CultureProcessTask.processCulture(i);
    }

}
