package ru.civwars.commands.town;

import java.util.List;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.town.Town;
import ru.civwars.town.TownMember;
import ru.lib27.annotation.NotNull;

public class CommandTownMods extends BasicCommand {

    private static final int MODS_IN_PAGE = 10;
    
    public CommandTownMods(@NotNull CivWars plugin) {
        super(plugin, "town_mods", new String[] {"mods"});
        this.setUsage("/town mods [page]");
    }

    @CommandHandler
    public void handle(KPlayer player, Integer page) {
        Town town = player.getTown();
        if (town == null) {
            return;
        }

        List<TownMember> members = town.getMembersList(1);

        int totalPage = (members.size() - 1) / MODS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * MODS_IN_PAGE, members.size());

        player.sendHeader("commands_town_towns_header", town.getName(), (page + 1), (totalPage + 1));
        for (int i = (page * MODS_IN_PAGE); i < lastIndex; i++) {
            TownMember member = members.get(i);
            
            player.sendMessage((i + 1) + ") " + member.getName());
        }

        if (page == 0 && totalPage > 0) {
            player.sendRow("commands_town_towns_footer");
        }
    }
    
    @CommandHandler
    public void handle(KPlayer player) {
        this.handle(player, 1);
    }
}
