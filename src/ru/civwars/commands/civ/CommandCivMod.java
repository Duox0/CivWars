package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivMod extends BasicCommand {

    public CommandCivMod(@NotNull CivWars plugin) {
        super(plugin, "civ_mod", new String[] {"mod"});
        this.setUsage("/civ mod <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getCiv() != null) {
            CivHandler.handleCivMod(player, playername);
        }
    }

}
