package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import ru.civwars.CivWars;
import ru.civwars.chat.Chat;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.lib27.annotation.NotNull;

public class ChatListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new ChatListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public ChatListener(@NotNull CivWars plugin) {
        super(plugin);
    }
    
    @EventHandler
    public void onChatMessageSend(AsyncPlayerChatEvent event) {
        KPlayer player = PlayerManager.getPlayer(event.getPlayer());
        
        String formattedMessage = event.getMessage();
        
        Chat.sendMessage(player, formattedMessage);
        System.out.println("<" + player.getName() + "> " + event.getMessage());
        
        event.setCancelled(true);
    }
    
}
