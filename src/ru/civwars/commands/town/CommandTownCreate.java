package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.handlers.TownHandler;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CommandTownCreate extends BasicCommand {

    public CommandTownCreate(@NotNull CivWars plugin) {
        super(plugin, "town_create", new String[]{"create"});
        this.setUsage("/town create <name> <capitalname>");
    }

    @CommandHandler
    public void handle(KPlayer player, String name) {
        if(player.getTown() == null) {
            TownHandler.handleTownCreate(player, name, 1);
        }
    }

}
