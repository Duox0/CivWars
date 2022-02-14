package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.lib27.annotation.NotNull;

public class CommandCiv extends BasicCommand {

    public CommandCiv(@NotNull CivWars plugin) {
        super(plugin, "civ", new String[]{"civ", "k"});
        this.setUsage("/civ");

        this.registerSubCommand(new CommandCivAccept(plugin));
        this.registerSubCommand(new CommandCivCreate(plugin));
        this.registerSubCommand(new CommandCivList(plugin));
        this.registerSubCommand(new CommandCivInfo(plugin));
        this.registerSubCommand(new CommandCivShow(plugin));
        this.registerSubCommand(new CommandCivTowns(plugin));
        this.registerSubCommand(new CommandCivRename(plugin));
        this.registerSubCommand(new CommandCivSetLeader(plugin));
        this.registerSubCommand(new CommandCivClaimLeader(plugin));
        this.registerSubCommand(new CommandCivMod(plugin));
        this.registerSubCommand(new CommandCivUnmod(plugin));
        this.registerSubCommand(new CommandCivMods(plugin));
        this.registerSubCommand(new CommandCivDeposit(plugin));
        this.registerSubCommand(new CommandCivWithdraw(plugin));
    }
}
