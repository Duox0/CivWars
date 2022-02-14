package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivClaimLeader extends BasicCommand {

    public CommandCivClaimLeader(@NotNull CivWars plugin) {
        super(plugin, "civ_claimleader", new String[] {"claimleader"});
        this.setUsage("/civ claimleader");
    }

    @CommandHandler
    public void handle(KPlayer player) {
        if(player.getCiv() != null) {
            CivHandler.handleCivClaimLeader(player);
        }
    }

}
