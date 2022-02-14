package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivWithdraw extends BasicCommand {

    public CommandCivWithdraw(@NotNull CivWars plugin) {
        super(plugin, "civ_withdraw", new String[] {"withdraw"});
        this.setUsage("/civ withdraw <amount>");
    }

    @CommandHandler
    public void handle(KPlayer player, Long amount) {
        if(player.getCiv() != null) {
            CivHandler.handleCivWithdrawGold(player, amount);
        }
    }

}
