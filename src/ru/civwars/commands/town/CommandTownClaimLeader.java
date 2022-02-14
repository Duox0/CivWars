package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownClaimLeader extends BasicCommand {

    public CommandTownClaimLeader(@NotNull CivWars plugin) {
        super(plugin, "town_claimleader", new String[] {"claimleader"});
        this.setUsage("/town claimleader");
    }

    @CommandHandler
    public void handle(KPlayer player) {
        if(player.getTown() != null) {
            TownHandler.handleTownClaimLeader(player);
        }
    }

}
