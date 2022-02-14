package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownLeave extends BasicCommand {

    public CommandTownLeave(@NotNull CivWars plugin) {
        super(plugin, "town_leave", new String[] {"leave"});
        this.setUsage("/town leave");
    }

    @CommandHandler
    public void handle(KPlayer player) {
        if(player.getTown() != null) {
            TownHandler.handleTownLeaveMember(player);
        }
    }

}
