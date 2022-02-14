package ru.civwars.commands.civ;

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
import ru.civwars.civ.Civilization;
import ru.civwars.civ.CivManager;
import ru.civwars.town.Town;
import ru.lib27.annotation.NotNull;

public class CommandCivTowns extends BasicCommand {

    private static final int TOWNS_IN_PAGE = 10;

    public CommandCivTowns(@NotNull CivWars plugin) {
        super(plugin, "civ_towns", new String[]{"towns"});
        this.setUsage("/civ towns <civ> [page]");
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name, Integer page) {
        Civilization civ = CivManager.getCiv(name);
        if (civ == null) {
            sender.sendError("civ_not_found_s1", name);
            return;
        }
        this.list(sender, civ, page);
    }

    @CommandHandler
    public void handle(AbstractCommandSender sender, String name) {
        this.handle(sender, name, 1);
    }

    @CommandHandler
    public void handle(KPlayer player, String name, Integer page) {
        this.handle((AbstractCommandSender) player, name, page);
    }

    @CommandHandler
    public void handle(KPlayer player, String name) {
        this.handle(player, name, 1);
    }

    @CommandHandler
    public void handle(KPlayer player, Integer page) {
        Civilization civ = player.getCiv();
        if (civ != null) {
            this.list(player, civ, page);
        }
    }

    @CommandHandler
    public void handle(KPlayer player) {
        this.handle(player, 1);
    }

    private void list(@NotNull AbstractCommandSender sender, @NotNull Civilization civ, int page) {
        Town capital = null;
        TreeMap<String, Town> civTowns = Maps.newTreeMap();
        TreeMap<String, Town> capturedTowns = Maps.newTreeMap();
        for (Town town : civ.getTownsList()) {
            if (civ.isCapital(town)) {
                capital = town;
            } else if (town.getMotherCiv() != null) {
                capturedTowns.put(town.getName(), town);
            } else {
                civTowns.put(town.getName(), town);
            }
        }

        civTowns.comparator();
        capturedTowns.comparator();

        List<Town> towns = Lists.newLinkedList();
        if (capital != null) {
            towns.add(capital);
        }
        towns.addAll(civTowns.values());
        towns.addAll(capturedTowns.values());

        int totalPage = (towns.size() - 1) / TOWNS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * TOWNS_IN_PAGE, towns.size());

        sender.sendHeader("commands_civ_towns_header", civ.getName(), (page + 1), (totalPage + 1));
        for (int i = (page * TOWNS_IN_PAGE); i < lastIndex; i++) {
            Town town = towns.get(i);

            if (civ.isCapital(town)) {
                sender.sendMessage((i + 1) + ") " + town.getName() + " " + ChatColor.GOLD + "Capital");
            } else if (town.getMotherCiv() != null) {
                sender.sendMessage((i + 1) + ") " + town.getName() + " " + ChatColor.YELLOW + "Captured");
            } else {
                sender.sendMessage((i + 1) + ") " + town.getName());
            }
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRow("commands_civ_towns_footer");
        }
    }
}
