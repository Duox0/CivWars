package ru.civwars.commands.mobs;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.ChatColor;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.chat.Chat;
import ru.civwars.command.CommandHandler;
import ru.civwars.entity.template.NPCTemplate;
import ru.civwars.init.NpcTypes;
import ru.lib27.annotation.NotNull;

public class CommandList extends BasicCommand {

    private static final int MOBS_IN_PAGE = 10;

    public CommandList(@NotNull CivWars plugin) {
        super(plugin, "list");
        this.setUsage("/mobs list [page]");
        this.setDescription("Get a list of mobs");
        this.setPermission("civcraft.commands.mobs.list");
    }

    @CommandHandler
    public void handle(@NotNull AbstractCommandSender sender, Integer page) {
        this.list(sender, page == null ? 1 : page);
    }

    @CommandHandler
    public void handle(@NotNull AbstractCommandSender sender) {
        this.list(sender, 1);
    }

    private void list(@NotNull AbstractCommandSender sender, int page) {
        List<NPCTemplate> mobs = Lists.newArrayList(NpcTypes.values());

        int totalPage = (mobs.size() - 1) / MOBS_IN_PAGE;
        page = Math.min(totalPage + 1, Math.max(1, page)) - 1;
        int lastIndex = Math.min((page + 1) * MOBS_IN_PAGE, mobs.size());

        sender.sendRawMessage(Chat.buildTitle("List of Mobs (" + (page + 1) + "/" + (totalPage + 1) + ")"));
        for (int i = (page * MOBS_IN_PAGE); i < lastIndex; i++) {
            NPCTemplate template = mobs.get(i);
            sender.sendRawMessage(template.getName() + ChatColor.GRAY + " (#" + template.getId() + ")");
        }

        if (page == 0 && totalPage > 0) {
            sender.sendRawMessage(ChatColor.GRAY + "Use '/mobs list [page]' to view the next page");
        }
    }

}
