package ru.civwars.chat.requests;

import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ru.civwars.TaskMaster;
import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;

public class ChatRequestManager {

    public static ChatRequestManager instance = new ChatRequestManager();

    private long nextId = 0;
    private final ConcurrentHashMap<Long, ChatRequest> requests = new ConcurrentHashMap<>();

    public ChatRequestManager() {

    }

    public long getNextId() {
        return this.nextId++;
    }

    /**
     * Создает новый запрос.
     *
     * @param request - запрос.
     * @param chatMessage
     * @param hoverMessage
     * @param clickCommand
     */
    public void sendRequest(@NotNull ChatRequest request, @NotNull String chatMessage, @NotNull String hoverMessage, @NotNull String clickCommand) {
        TextComponent text = new TextComponent(chatMessage);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand + " " + request.getId()));
        text.setBold(true);
        text.setItalic(true);
        text.setColor(ChatColor.LIGHT_PURPLE);
        request.getReceiver().sendMessage(text);
        this.requests.put(request.getId(), request);

        TaskMaster.runTask(() -> {
            ChatRequest request2 = this.requests.get(request.getId());
            if (request2 != null) {
                if (request2.isCompleted()) {
                    this.requests.remove(request.getId());
                } else if (request2.isExpired()) {
                    request2.cancel(null);
                    this.requests.remove(request.getId());
                }
            }
        });
    }

    public boolean process(@NotNull KPlayer receiver, long id, boolean flag, String[] args) {
        ChatRequest request = this.requests.get(id);
        if (request != null) {
            if (request.process(receiver, flag, args)) {
                this.requests.remove(id);
            }
            return true;
        }
        return false;
    }

}
