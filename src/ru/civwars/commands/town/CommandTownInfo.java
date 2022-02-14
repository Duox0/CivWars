package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.town.Town;
import ru.lib27.annotation.NotNull;

public class CommandTownInfo extends BasicCommand {

    public CommandTownInfo(@NotNull CivWars plugin) {
        super(plugin, "town_info", new String[]{"info"});
        this.setUsage("/town info");
    }

    @CommandHandler
    public void handle(KPlayer player) {
        Town town = player.getTown();
        if (town != null) {
            CommandTownShow.sendTooltip(player, town);
        }
    }
    
}
