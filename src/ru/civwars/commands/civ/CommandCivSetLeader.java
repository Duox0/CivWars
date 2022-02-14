package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivSetLeader extends BasicCommand {

    public CommandCivSetLeader(@NotNull CivWars plugin) {
        super(plugin, "civ_setleader", new String[] {"setleader"});
        this.setUsage("/civ setleader <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getCiv() != null) {
            CivHandler.handleCivChangeLeader(player, playername);
        }
    }

}
