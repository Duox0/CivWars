package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownSetLeader extends BasicCommand {

    public CommandTownSetLeader(@NotNull CivWars plugin) {
        super(plugin, "town_setleader", new String[] {"setleader"});
        this.setUsage("/town setleader <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getTown() != null) {
            TownHandler.handleTownChangeLeader(player, playername);
        }
    }

}
