package ru.civwars.commands.town;

import ru.civwars.CivWars;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerCache;
import ru.civwars.entity.player.PlayerCacheEntry;
import ru.civwars.town.RankRights;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.town.TownMember;
import ru.lib27.annotation.NotNull;

public class CommandTownShow extends BasicCommand {

    public CommandTownShow(@NotNull CivWars plugin) {
        super(plugin, "town_show", new String[]{"show"});
        this.setUsage("/town show <town>");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name) {
        Town town = TownManager.getTown(name);
        if (town == null) {
            sender.sendError("town_not_found_s1", name);
            return;
        }
        CommandTownShow.sendTooltip(sender, town);
    }

    public static void sendTooltip(@NotNull AbstractCommandSender sender, @NotNull Town town) {
        KPlayer player = null;
        TownMember memberMe = null;

        if (sender instanceof KPlayer) {
            player = (KPlayer) sender;
            memberMe = town.getCiv().getMember(player.getObjectId());
        }

        boolean isOP = player != null ? player.isOp() : true;

        sender.sendHeader("commands_town_show_header", town.getName());

        PlayerCacheEntry leader =  town.getLeaderId() != null ? PlayerCache.get(town.getLeaderId()) : null;
        sender.sendRow("commands_town_show_master", leader!= null ? leader.getName() : "");

        if (player == null || memberMe != null) {
            sender.sendRow("commands_town_show_online", town.getOnlineMembersList().size());
        }

        if (player == null || town.hasRankRights(player, RankRights.TR_RIGHT_WITHDRAW_GOLD)) {
            sender.sendRow("commands_town_show_treasury", town.getGold());
        }
    }
    
}
