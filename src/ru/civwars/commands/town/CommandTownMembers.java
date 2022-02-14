package ru.civwars.commands.town;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import ru.civwars.CivWars;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.civ.CivDefaultRanks;
import ru.civwars.civ.Civilization;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.town.TownMember;
import ru.lib27.annotation.NotNull;

public class CommandTownMembers extends BasicCommand {

    private static final int MEMBERS_IN_PAGE = 10;

    public CommandTownMembers(@NotNull CivWars plugin) {
        super(plugin, "town_towns", new String[]{"members"});
        this.setUsage("/town members [page]");
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
        TreeMap<String, TownMember> leaders = Maps.newTreeMap();
        TreeMap<String, TownMember> officers = Maps.newTreeMap();
        TreeMap<String, TownMember> others = Maps.newTreeMap();

        for (TownMember member : town.getMembersList()) {
            if (member.getRankId() == 1) {
                leaders.put(member.getName(), member);
            } else if (member.getRankId() == 1) {
                officers.put(member.getName(), member);
            } else {
                others.put(member.getName(), member);
            }
        }

        leaders.comparator();
        officers.comparator();
        others.comparator();

        List<TownMember> members = Lists.newLinkedList();
        members.addAll(leaders.values());
        members.addAll(officers.values());
        members.addAll(others.values());

        int totalPage = (members.size() - 1) / MEMBERS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * MEMBERS_IN_PAGE, members.size());

        sender.sendHeader("commands_town_members_header", town.getName(), (page + 1), (totalPage + 1));
        for (int i = (page * MEMBERS_IN_PAGE); i < lastIndex; i++) {
            TownMember member = members.get(i);

            if (member.getRankId() == 1) {
                sender.sendMessage((i + 1) + ") " + member.getName() + " " + ChatColor.GOLD + "Leader");
            } else if (member.getRankId() == 1) {
                sender.sendMessage((i + 1) + ") " + member.getName() + " " + ChatColor.YELLOW + "Officer");
            } else {
                sender.sendMessage((i + 1) + ") " + member.getName());
            }
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_town_members_footer");
        }
    }
}
