package ru.civwars.commands;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CommandBalance extends BasicCommand {

    public CommandBalance(@NotNull CivWars plugin) {
        super(plugin, new String[] {"balance", "bal"});
        this.setUsage("/balance");
        this.setDescription("Gets your current money balance");
        this.setPermission("civcraft.commands.balance");
    }

    @CommandHandler
    public void run(KPlayer player) {
        player.changeGold(Long.MAX_VALUE);
        player.sendMessage("commands_balance_current", player.getGold());
    }

}
