package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownRename extends BasicCommand {

    public CommandTownRename(@NotNull CivWars plugin) {
        super(plugin, "town_rename", new String[] {"rename"});
        this.setUsage("/town rename <name>");
    }

    @CommandHandler
    public void handle(KPlayer player, String name) {
        if(player.getTown() != null) {
            TownHandler.handleTownChangeName(player, name);
        }
    }

}
