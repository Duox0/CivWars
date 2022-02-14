package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivUnmod extends BasicCommand {

    public CommandCivUnmod(@NotNull CivWars plugin) {
        super(plugin, "civ_unmod", new String[] {"unmod"});
        this.setUsage("/civ unmod <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getCiv() != null) {
            CivHandler.handleCivUnmod(player, playername);
        }
    }

}
