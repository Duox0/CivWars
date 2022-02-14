package ru.civwars.chat;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.lib27.annotation.NotNull;

public class Chat {
    
    private static final int TITLE_MAX_LENGTH = 50;
    
    @NotNull
    public static String centerText(@NotNull String text, int centerPos) {
        StringBuilder centered = new StringBuilder();
        
        int msgLength = 0;
        boolean prevCode = false;
        boolean isBold = false;
        
        for (char c : text.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode == true) {
                prevCode = false;
                isBold = (c == 'l' || c == 'L');
            } else {
                msgLength += 1;
            }
        }
        
        int whitePxSize = (centerPos * 2 - msgLength);
        int spaceCount = whitePxSize / 2 / (1);
        
        centered.append(Strings.repeat(" ", spaceCount));
        centered.append(text);
        
        return centered.toString();
    }
    
    @NotNull
    public static String buildTitle(@NotNull String title) {
        StringBuilder centered = new StringBuilder();
        
        int titleLength = 0;
        boolean prevCode = false;
        
        for (char c : title.toCharArray()) {
            if (c == '§') {
                prevCode = true;
            } else if (prevCode == true) {
                prevCode = false;
            } else {
                titleLength += 1;
            }
        }
        
        int leftCount = Math.max(3, (TITLE_MAX_LENGTH - titleLength) / 2);
        
        centered.append(ChatColor.AQUA).append(Strings.repeat("=", leftCount - 2)).append("[ ");
        centered.append(ChatColor.YELLOW).append(title);
        centered.append(ChatColor.AQUA).append(" ]").append(Strings.repeat("=", leftCount - 2));
        
        return centered.toString();
    }
    
    public static void sendMessage(@NotNull KPlayer player, @NotNull String message) {
        message = message + "1";
        for(KPlayer p : PlayerManager.getPlayers()) {
            p.sendRawMessage(message);
        }
    }
}
