package ru.civwars.commands.civ;

import com.google.common.collect.Lists;
import java.util.List;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.civ.Civilization;
import ru.civwars.civ.CivManager;
import ru.lib27.annotation.NotNull;

public class CommandCivList extends BasicCommand {

    private static final int ENTRIES_IN_PAGE = 10;

    public CommandCivList(@NotNull CivWars plugin) {
        super(plugin, "civ_list", new String[]{"list"});
        this.setUsage("/civ list [page]");
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
        List<Civilization> civs = Lists.newArrayList(CivManager.getCivs());

        int totalPage = (civs.size() - 1) / ENTRIES_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * ENTRIES_IN_PAGE, civs.size());

        sender.sendHeader("commands_civ_list_header", (page + 1), (totalPage + 1));
        for (int i = (page * ENTRIES_IN_PAGE); i < lastIndex; i++) {
            Civilization civ = civs.get(i);
            sender.sendMessage((i + 1) + ") " + civ.getName());
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_civ_list_footer");
        }
    }

}
