package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownMod extends BasicCommand {

    public CommandTownMod(@NotNull CivWars plugin) {
        super(plugin, "town_mod", new String[] {"mod"});
        this.setUsage("/town mod <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getTown() != null) {
            TownHandler.handleTownPromote(player, playername);
        }
    }

}
