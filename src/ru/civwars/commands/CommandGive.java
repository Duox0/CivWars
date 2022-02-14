package ru.civwars.commands;

import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import ru.civwars.i18n.I18n;
import ru.civwars.CivWars;
import ru.civwars.command.BasicCommand;
import ru.civwars.bukkit.AbstractCommandSender;
import ru.civwars.command.CommandHandler;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CommandGive extends BasicCommand {

    public CommandGive(@NotNull CivWars plugin) {
        super(plugin, "give");
        this.setUsage("/give <player> <item> [amount]");
        this.setDescription("Give a player an item");
        this.setPermission("civcraft.commands.give");
    }

    @CommandHandler
    public void handle(@NotNull AbstractCommandSender sender, @NotNull KPlayer target, @NotNull String itemId, @Nullable Integer count) {
        String fixItemId;
        short metadata = 0;
        if (itemId.contains(":")) {
            String[] split = StringUtils.split(itemId, ":");
            fixItemId = split[0];
            try {
                metadata = Short.parseShort(split[1]);
            } catch (NumberFormatException ex) {
            }
        } else {
            fixItemId = itemId;
        }

        Material bukkitItem = Material.getMaterial(fixItemId.toUpperCase());
        if (bukkitItem != null) {
            this.give(sender, target, bukkitItem, metadata, count != null ? count : 1, false);
            return;
        }

        CustomItem customItem;
        try {
            customItem = CustomItems.get(Integer.parseInt(itemId));
        } catch (NumberFormatException ex) {
            customItem = CustomItems.get(itemId);
        }

        if (customItem != null) {
            this.give(sender, target, customItem, count != null ? count : 1, false);
            return;
        }

        sender.sendError("unknownItemId", itemId);
    }

    private void give(@NotNull AbstractCommandSender sender, @NotNull KPlayer target, @NotNull Material item, short metadata, int count, boolean isDropItemsIfFull) {
        if (item == Material.AIR) {
            sender.sendError("commands.give.failed", item.toString().toLowerCase(Locale.ENGLISH).replace('_', ' '), target.getName());
            return;
        }

        int fixCount = Math.max(1, count);
        int fixData = metadata;
        if (item.getMaxDurability() > 0) {
            fixData = Math.max(0, Math.min(item.getMaxDurability(), metadata));
        }

        ItemStack stack = new ItemStack(item, fixCount, (short) fixData);

        String itemName = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        sender.sendRawMessage(I18n.tl("commands.give.success", itemName, count, target.getName()));

        Map<Integer, ItemStack> leftovers = target.getEntity().getInventory().addItem(stack);
        if (isDropItemsIfFull) {
            for (ItemStack stack2 : leftovers.values()) {
                World world = target.getEntity().getWorld();
                world.dropItemNaturally(target.getLocation(), stack2);
            }
        } else {
            int lost = leftovers.keySet().stream().mapToInt(i -> i).sum();
            if (lost > 0) {
                sender.sendRawMessage(I18n.tl("commands.give.success.lost", itemName, lost, target.getName()));
            }
        }
    }

    private void give(@NotNull AbstractCommandSender sender, @NotNull KPlayer target, @NotNull CustomItem item, int count, boolean isDropItemsIfFull) {
        int fixCount = Math.max(1, count);
        ItemStack stack = item.createItemStack(fixCount);

        sender.sendRawMessage(I18n.tl("commands.give.success", item.getName(), count, target.getName()));

        Map<Integer, ItemStack> leftovers = target.getEntity().getInventory().addItem(stack);

        if (isDropItemsIfFull) {
            for (ItemStack stack2 : leftovers.values()) {
                World world = target.getEntity().getWorld();
                world.dropItemNaturally(target.getLocation(), stack2);
            }
        } else {
            int lost = leftovers.keySet().stream().mapToInt(i -> i).sum();
            if (lost > 0) {
                sender.sendRawMessage(I18n.tl("commands.give.success.lost", item.getName(), lost, target.getName()));
            }
        }
    }

}
