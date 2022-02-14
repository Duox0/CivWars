package ru.civwars.commands.admin;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.commands.admin.faction.CommandAdFaction;
import ru.civwars.commands.admin.town.CommandTown;
import ru.lib27.annotation.NotNull;

public class CommandAdmin extends BasicCommand {

    public CommandAdmin(@NotNull CivWars plugin) {
        super(plugin, new String[]{"admin", "ad"});
        this.setUsage("/admin");
        this.setDescription("Admin commands");
        this.setPermission("civcraft.commands.admin");

        this.registerSubCommand(new CommandAdFaction(plugin));
        this.registerSubCommand(new CommandTown(plugin));
    }
}
