package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownDeposit extends BasicCommand {

    public CommandTownDeposit(@NotNull CivWars plugin) {
        super(plugin, "town_deposit", new String[] {"deposit"});
        this.setUsage("/town deposit <amount>");
    }

    @CommandHandler
    public void handle(KPlayer player, Long amount) {
        if(player.getTown() != null) {
            TownHandler.handleTownDepositGold(player, amount);
        }
    }

}
