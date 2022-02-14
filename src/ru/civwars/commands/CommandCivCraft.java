package ru.civwars.commands;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivCraft extends BasicCommand {

    public CommandCivCraft(@NotNull CivWars plugin) {
        super(plugin, "civcraft");
        this.setUsage("/civcraft");
        this.setDescription("CivCraft commands");
        this.setPermission("civcraft.commands.civcraft");
    }

    @CommandHandler
    public void run(@NotNull AbstractCommandSender sender) {
    }

}
