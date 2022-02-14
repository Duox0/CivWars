package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownKick extends BasicCommand {

    public CommandTownKick(@NotNull CivWars plugin) {
        super(plugin, "town_kick", new String[] {"kick"});
        this.setUsage("/town kick <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getTown() != null) {
            TownHandler.handleTownKickMember(player, playername);
        }
    }

}
