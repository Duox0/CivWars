package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.civ.Civilization;
import ru.lib27.annotation.NotNull;

public class CommandCivInfo extends BasicCommand {

    public CommandCivInfo(@NotNull CivWars plugin) {
        super(plugin, "civ_info", new String[]{"info"});
        this.setUsage("/civ info");
    }

    @CommandHandler
    public void handle(KPlayer player) {
        Civilization civ = player.getCiv();
        if (civ != null) {
            CommandCivShow.sendTooltip(player, civ);
        }
    }
    
}
