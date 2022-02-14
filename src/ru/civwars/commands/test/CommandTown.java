package ru.civwars.commands.test;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Block;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.building.block.BuildingBlock;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.schematic.Schematic;
import ru.civwars.schematic.block.SchematicBlock;
import ru.civwars.schematic.SchematicManager;
import ru.civwars.thread.request.sync.RequestBuildSchematicBlock;
import ru.civwars.town.Town;
import ru.civwars.util.BlockPos;
import ru.civwars.util.EnumFacing;
import ru.civwars.util.LocationUtils;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class CommandTown extends BasicCommand {

    public CommandTown(@NotNull CivWars plugin) {
        super(plugin, "town");
        this.setUsage("/test town <name> [filename] [world] [x] [y] [z]");
        this.setDescription("Create new test town");
        this.setPermission("civcraft.commands.test.town");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String worldname, Integer x, Integer y, Integer z) {
        CivWorld world = WorldManager.getWorld(worldname);
        if (world == null) {
            return;
        }

        BlockPos pos = new BlockPos(x, y, z);
        Random rand = new Random();

        BuildingBlock block = world.getBuildingBlock(pos);
        sender.sendRawMessage("blocks=" + world.getBuildingBlocks().size());
        int i = 0;
        for (BuildingBlock bb : world.getBuildingBlocks()) {
            sender.sendRawMessage(bb.toString());
            i++;
            if (i >= 5) {
                break;
            }
        }
        sender.sendRawMessage(block != null ? block.toString() : "block null");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name, String filename, String worldname, Integer x, Integer y, Integer z) {
        CivWorld world = WorldManager.getWorld(worldname);
        if (world == null) {
            return;
        }

        this.createTown(sender, name, world, x, y, z, EnumFacing.EAST, filename);
    }

    @CommandHandler
    public void handle(KPlayer player, String name) {
        CivWorld world = player.getWorld();
        Location location = player.getLocation();

        this.createTown(player, name, world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), EnumFacing.fromAngle(location.getYaw()), "capitol");
    }

    public void createTown(@NotNull AbstractCommandSender sender, @NotNull String name, @NotNull CivWorld world, int x, int y, int z, @NotNull EnumFacing facing, @NotNull String filename) {
        String filepath = "templates/themes/default/structures/" + filename + "/" + filename + "_east.def";

        Schematic schematic = SchematicManager.getSchematic(filepath);
        if (schematic == null) {
            return;
        }

        Location location = new Location(world.getWorld(), x, y, z);
        Location corner = LocationUtils.repositionCorner(location, facing, schematic.getSize().width, schematic.getSize().length);

        x = corner.getBlockX();
        y = corner.getBlockY();
        z = corner.getBlockZ();

        Town town = null;
        if (sender instanceof KPlayer) {
            KPlayer player = (KPlayer) sender;
            //town = CivWars.getTownManager().createTown(player, name, world, x >> 4, z >> 4, y, facing, schematic);
        } else {
            //town = CivWars.getTownManager().createTown(name, world, x >> 4, z >> 4, y, facing, schematic);
        }

        if (town == null) {
        }
    }

}
