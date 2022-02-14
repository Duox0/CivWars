package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.handlers.CivHandler;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CommandCivCreate extends BasicCommand {

    public CommandCivCreate(@NotNull CivWars plugin) {
        super(plugin, "civ_create", new String[]{"create"});
        this.setUsage("/civ create <name> <capitalname>");
    }

    @CommandHandler
    public void handle(KPlayer player, String name, String capitalName) {
        if(player.getTown() == null) {
            CivHandler.handleCivCreate(player, name, capitalName, 1, "default");
        }
    }

}
