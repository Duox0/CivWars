package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.CivHandler;
import ru.lib27.annotation.NotNull;

public class CommandCivRename extends BasicCommand {

    public CommandCivRename(@NotNull CivWars plugin) {
        super(plugin, "civ_rename", new String[] {"rename"});
        this.setUsage("/civ rename <name>");
    }

    @CommandHandler
    public void handle(KPlayer player, String name) {
        if(player.getCiv() != null) {
            CivHandler.handleCivChangeName(player, name);
        }
    }

}
