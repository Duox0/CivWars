package ru.civwars.commands.town;

import com.google.common.collect.Lists;
import java.util.List;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.lib27.annotation.NotNull;

public class CommandTownList extends BasicCommand {

    private static final int ENTRIES_IN_PAGE = 10;

    public CommandTownList(@NotNull CivWars plugin) {
        super(plugin, "town_list", new String[]{"list"});
        this.setUsage("/town list [page]");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, Integer page) {
        this.list(sender, page == null ? 1 : page);
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender) {
        this.list(sender, 1);
    }

    private void list(@NotNull AbstractCommandSender sender, int page) {
        List<Town> towns = Lists.newArrayList(TownManager.getTowns());

        int totalPage = (towns.size() - 1) / ENTRIES_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * ENTRIES_IN_PAGE, towns.size());

        sender.sendHeader("commands_town_list_header", (page + 1), (totalPage + 1));
        for (int i = (page * ENTRIES_IN_PAGE); i < lastIndex; i++) {
            Town town = towns.get(i);
            sender.sendMessage((i + 1) + ") " + town.getName() + " (" + town.getCiv().getName() +")");
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_town_list_footer");
        }
    }

}
