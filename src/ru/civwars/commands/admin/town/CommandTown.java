package ru.civwars.commands.admin.town;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.lib27.annotation.NotNull;

public class CommandTown extends BasicCommand {

    public CommandTown(@NotNull CivWars plugin) {
        super(plugin, new String[]{"town", "t"});
        this.setUsage("/admin town");
        this.setDescription("Admin town commands");
        this.setPermission(null);
    }
}
