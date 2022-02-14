package ru.civwars.commands.civ;

import java.util.List;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.civ.CivDefaultRanks;
import ru.civwars.civ.Civilization;
import ru.civwars.town.TownMember;
import ru.lib27.annotation.NotNull;

public class CommandCivMods extends BasicCommand {

    private static final int MODS_IN_PAGE = 10;
    
    public CommandCivMods(@NotNull CivWars plugin) {
        super(plugin, "civ_mods", new String[] {"mods"});
        this.setUsage("/civ mods [page]");
    }

    @CommandHandler
    public void handle(KPlayer player, Integer page) {
        Civilization civ = player.getCiv();
        if (civ == null) {
            return;
        }

        List<TownMember> members = civ.getMembersList(1);

        int totalPage = (members.size() - 1) / MODS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * MODS_IN_PAGE, members.size());

        player.sendHeader("commands_civ_towns_header", civ.getName(), (page + 1), (totalPage + 1));
        for (int i = (page * MODS_IN_PAGE); i < lastIndex; i++) {
            TownMember member = members.get(i);
            
            player.sendMessage((i + 1) + ") " + member.getName());
        }

        if (page == 0 && totalPage > 0) {
            player.sendRow("commands_civ_towns_footer");
        }
    }
    
    @CommandHandler
    public void handle(KPlayer player) {
        this.handle(player, 1);
    }
}
