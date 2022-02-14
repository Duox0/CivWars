package ru.civwars.commands.civ;

import ru.civwars.CivWars;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.BasicCommand;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerCache;
import ru.civwars.entity.player.PlayerCacheEntry;
import ru.civwars.civ.Civilization;
import ru.civwars.civ.CivManager;
import ru.civwars.town.RankRights;
import ru.civwars.town.Town;
import ru.civwars.town.TownManager;
import ru.civwars.town.TownMember;
import ru.lib27.annotation.NotNull;

public class CommandCivShow extends BasicCommand {

    public CommandCivShow(@NotNull CivWars plugin) {
        super(plugin, "civ_show", new String[]{"show"});
        this.setUsage("/civ show <civ>");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name) {
        Civilization civ = CivManager.getCiv(name);
        if (civ == null) {
            sender.sendError("civ_not_found_s1", name);
            return;
        }
        CommandCivShow.sendTooltip(sender, civ);
    }

    public static void sendTooltip(@NotNull AbstractCommandSender sender, @NotNull Civilization civ) {
        KPlayer player = null;
        TownMember memberMe = null;

        if (sender instanceof KPlayer) {
            player = (KPlayer) sender;
            memberMe = civ.getMember(player.getObjectId());
        }

        boolean isOP = player != null ? player.isOp() : true;

        sender.sendHeader("commands_civ_show_header", civ.getName());

        Town capital = civ.getCapitalId() != null ? TownManager.getTown(civ.getCapitalId()) : null;
        sender.sendRow("commands_civ_show_capital", capital != null ? capital.getName() : "");

        PlayerCacheEntry leader =  civ.getLeaderId() != null ? PlayerCache.get(civ.getLeaderId()) : null;
        sender.sendRow("commands_civ_show_master", leader!= null ? leader.getName() : "");

        if (player == null || memberMe != null) {
            sender.sendRow("commands_civ_show_online", civ.getOnlineMembersList().size());
        }

        if (player == null || civ.hasRankRights(player, RankRights.TR_RIGHT_WITHDRAW_GOLD)) {
            sender.sendRow("commands_civ_show_treasury", civ.getGold());
        }
        
        sender.sendRow("commands_civ_show_towns", civ.getTownCount());
    }
    
}
