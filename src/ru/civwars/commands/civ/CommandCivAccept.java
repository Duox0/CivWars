package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.chat.requests.ChatRequestManager;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CommandCivAccept extends BasicCommand {

    public CommandCivAccept(@NotNull CivWars plugin) {
        super(plugin, "civ_accept", new String[] {"accept"});
        this.setUsage("/civ accept <requestid>");
    }

    @CommandHandler
    public void handle(KPlayer player, Long id) {
        ChatRequestManager.instance.process(player, id, false, new String[0]);
    }

}
