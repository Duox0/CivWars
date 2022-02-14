package ru.civwars.commands.admin.faction;

import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.lib27.annotation.NotNull;

public class CommandAdFaction extends BasicCommand {

    public CommandAdFaction(@NotNull CivWars plugin) {
        super(plugin, new String[]{"faction", "f"});
        this.setUsage("/admin faction");
        this.setDescription("Admin faction commands");
        this.setPermission(null);
    }
}
