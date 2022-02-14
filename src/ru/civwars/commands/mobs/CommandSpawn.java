package ru.civwars.commands.mobs;

import org.bukkit.Location;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.init.NpcTypes;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.world.CivWorld;
import ru.civwars.world.WorldManager;
import ru.lib27.annotation.NotNull;

public class CommandSpawn extends BasicCommand {

    public CommandSpawn(@NotNull CivWars plugin) {
        super(plugin, "spawn");
        this.setUsage("/mobs spawn <mob> [worldname] [x] [y] [z]");
        this.setDescription("Spawn a mob");
        this.setPermission("civcraft.commands.mobs.spawn");
    }

    @CommandHandler
    public void handle(@NotNull AbstractCommandSender sender, Integer mobId, String worldname, Double x, Double y, Double z) {
        CivWorld world = WorldManager.getWorld(worldname);
        if (world == null) {
            sender.sendMessage("World '" + worldname + "' not exist");
            return;
        }

        this.spawn(sender, mobId, world, x, y, z);
    }

    @CommandHandler
    public void handle(@NotNull KPlayer player, Integer mobId) {
        Location location = player.getLocation();
        this.spawn(player, mobId, player.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    private void spawn(@NotNull AbstractCommandSender sender, int mobId, @NotNull CivWorld world, double x, double y, double z) {
        NPCTemplate mob = NpcTypes.get(mobId);
        if (mob == null) {
            sender.sendMessage("Unknown mob type '" + mobId + "'");
            return;
        }

        if (world.spawn(mob, x, y, z) != null) {
            sender.sendMessage("Mob '" + mob.getName() + "' successfully spawned");
        } else {
            sender.sendMessage("Unable to spawn mob");
        }
    }

}
