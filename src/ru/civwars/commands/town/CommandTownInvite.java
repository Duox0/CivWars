package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.handlers.TownHandler;
import ru.lib27.annotation.NotNull;

public class CommandTownInvite extends BasicCommand {

    public CommandTownInvite(@NotNull CivWars plugin) {
        super(plugin, "town_invite", new String[] {"invite"});
        this.setUsage("/town invite <playername>");
    }

    @CommandHandler
    public void handle(KPlayer player, String playername) {
        if(player.getTown() != null) {
            TownHandler.handleTownInviteMember(player, playername);
        }
    }

}
