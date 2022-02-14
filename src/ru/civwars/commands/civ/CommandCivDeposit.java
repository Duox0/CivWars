package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivDeposit extends BasicCommand {

    public CommandCivDeposit(@NotNull CivWars plugin) {
        super(plugin, "civ_deposit", new String[] {"deposit"});
        this.setUsage("/civ deposit <amount>");
    }

    @CommandHandler
    public void handle(KPlayer player, Long amount) {
        if(player.getCiv() != null) {
            CivHandler.handleCivDepositGold(player, amount);
        }
    }

}
