package ru.civwars.commands.town;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.TreeMap;
import ru.civwars.CivWars;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.civ.Civilization;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.town.TownMember;
import ru.civwars.town.TownRank;
import ru.lib27.annotation.NotNull;

public class CommandTownRanks extends BasicCommand {

    private static final int RANKS_IN_PAGE = 10;

    public CommandTownRanks(@NotNull CivWars plugin) {
        super(plugin, "town_ranks", new String[]{"ranks"});
        this.setUsage("/town ranks [page]");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name, Integer page) {
        Town town = TownManager.getTown(name);
        if (town == null) {
            sender.sendError("town_not_found_s1", name);
            return;
        }
        this.list(sender, town, page);
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name) {
        this.handle(sender, name, 1);
    }

    @CommandHandler
    public void handle(KPlayer player, String name, Integer page) {
        Town town = TownManager.getTown(name);
        if (town == null) {
            player.sendError("town_not_found_s1", name);
            return;
        }

        Civilization civ = player.getCiv();
        TownMember memberMe = civ != null ? civ.getMember(player.getObjectId()) : null;
        if (civ == null) {
            player.sendError("you_dont_have_rights");
            return;
        }

        this.list(player, town, page);
    }

    @CommandHandler
    public void handle(KPlayer player, Integer page) {
        Town town = player.getTown();
        if (town != null) {
            TownMember memberMe = town.getCiv().getMember(player.getObjectId());
            if (memberMe == null) {
                player.sendError("you_dont_have_rights");
                return;
            }

            this.list(player, town, page);
        }
    }

    @CommandHandler
    public void handle(KPlayer player) {
        this.handle(player, 1);
    }

    private void list(@NotNull AbstractCommandSender sender, @NotNull Town town, int page) {
        TreeMap<Integer, TownRank> mapRanks = Maps.newTreeMap();
        for(TownRank rank : town.getRanksList()) {
            mapRanks.put(rank.getId(), rank);
        }
        
        mapRanks.comparator();

        List<TownRank> ranks = Lists.newLinkedList(mapRanks.values());

        int totalPage = (ranks.size() - 1) / RANKS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * RANKS_IN_PAGE, ranks.size());

        sender.sendHeader("commands_town_ranks_header", town.getName(), (page + 1), (totalPage + 1));
        for (int i = (page * RANKS_IN_PAGE); i < lastIndex; i++) {
            TownRank rank = ranks.get(i);
            sender.sendMessage(rank.getName() + " (#" + rank.getId() + ")");
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_town_ranks_footer");
        }
    }
}
