package ru.civwars.commands;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class CommandShow extends BasicCommand {

    public CommandShow(@NotNull CivWars plugin) {
        super(plugin, new String[]{"show", "show"});
        this.setUsage("/show");
        this.setDescription("Gets your current money balance");
        this.setPermission("civcraft.commands.balance");
    }

    @CommandHandler
    public void run(KPlayer player) {
        player.sendRawMessage("Civ: " + (player.getCiv()!= null ? player.getCiv().getName() : "null"));
    }

}
