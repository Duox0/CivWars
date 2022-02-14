package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownWithdraw extends BasicCommand {

    public CommandTownWithdraw(@NotNull CivWars plugin) {
        super(plugin, "town_withdraw", new String[] {"withdraw"});
        this.setUsage("/town withdraw <amount>");
    }

    @CommandHandler
    public void handle(KPlayer player, Long amount) {
        if(player.getTown() != null) {
            TownHandler.handleTownWithdrawGold(player, amount);
        }
    }

}
