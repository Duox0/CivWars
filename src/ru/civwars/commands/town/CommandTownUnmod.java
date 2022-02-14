package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownUnmod extends BasicCommand {

    public CommandTownUnmod(@NotNull CivWars plugin) {
        super(plugin, "town_unmod", new String[]{"unmod"});
        this.setUsage("/town unmod <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if (player.getTown() != null) {
            TownHandler.handleTownDemote(player, playername);
        }
    }

}
