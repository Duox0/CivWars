package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.lib27.annotation.NotNull;

public class CommandTown extends BasicCommand {

    public CommandTown(@NotNull CivWars plugin) {
        super(plugin, "town", new String[]{"town", "t"});
        this.setUsage("/town");

        this.registerSubCommand(new CommandTownAccept(plugin));
        this.registerSubCommand(new CommandTownCreate(plugin));
        this.registerSubCommand(new CommandTownList(plugin));
        this.registerSubCommand(new CommandTownInfo(plugin));
        this.registerSubCommand(new CommandTownShow(plugin));
        this.registerSubCommand(new CommandTownRename(plugin));
        this.registerSubCommand(new CommandTownSetLeader(plugin));
        this.registerSubCommand(new CommandTownClaimLeader(plugin));
        this.registerSubCommand(new CommandTownInvite(plugin));
        this.registerSubCommand(new CommandTownLeave(plugin));
        this.registerSubCommand(new CommandTownKick(plugin));
        this.registerSubCommand(new CommandTownMembers(plugin));
        this.registerSubCommand(new CommandTownRanks(plugin));
        this.registerSubCommand(new CommandTownMod(plugin));
        this.registerSubCommand(new CommandTownUnmod(plugin));
        this.registerSubCommand(new CommandTownMods(plugin));
        this.registerSubCommand(new CommandTownDeposit(plugin));
        this.registerSubCommand(new CommandTownWithdraw(plugin));
    }
}
