package ru.civwars.commands.mobs;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.lib27.annotation.NotNull;

public class CommandMobs extends BasicCommand {

    public CommandMobs(@NotNull CivWars plugin) {
        super(plugin, "mobs");
        this.setUsage("/mobs");
        this.setDescription("Mobs commands");
        this.setPermission("civcraft.commands.mobs");
        
        this.registerSubCommand(new CommandList(plugin));
        this.registerSubCommand(new CommandSpawn(plugin));
    }

}
