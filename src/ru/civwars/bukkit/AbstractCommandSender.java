package ru.civwars.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.civwars.i18n.I18n;
import ru.civwars.chat.Chat;
import ru.lib27.annotation.NotNull;

public interface AbstractCommandSender extends CommandSender {

    public void sendRawMessage(@NotNull String message);

    public void sendRawMessage(@NotNull String[] messages);

    public default void sendHeader(@NotNull String key, Object... objects) {
        this.sendMessage(Chat.buildTitle(I18n.tl(key, objects)));
    }

    public default void sendRow(@NotNull String key, Object... objects) {
        this.sendMessage(I18n.tl(key, objects));
    }

    public default void sendSuccess(@NotNull String key, Object... objects) {
        this.sendMessage(ChatColor.GREEN + I18n.tl(key, objects));
    }

    public default void sendError(@NotNull String key, Object... objects) {
        this.sendMessage(ChatColor.RED + I18n.tl(key, objects));
    }

}
