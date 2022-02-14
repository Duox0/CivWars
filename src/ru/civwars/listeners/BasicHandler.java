package ru.civwars.listeners;

import java.util.Random;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import ru.civwars.CivWars;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.entity.player.PlayerManager;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public abstract class BasicHandler<T extends Event> {

    protected final Random random = new Random();
    
    protected final CivWars plugin;
    private final Class<T> eventClass;
    
    public BasicHandler(@NotNull CivWars plugin, @NotNull Class<T> eventClass) {
        this.plugin = plugin;
        this.eventClass = eventClass;
    }

    @NotNull
    public Class<T> getEventClass() {
        return this.eventClass;
    }
    
    protected abstract void handle(@NotNull T event);

    @Nullable
    protected final KPlayer getPlayer(@NotNull UUID playerId) {
        return PlayerManager.getPlayer(playerId);
    }

    @Nullable
    protected final KPlayer getPlayer(@NotNull String playerName) {
        return PlayerManager.getPlayer(playerName);
    }

    @Nullable
    protected final KPlayer getPlayer(@NotNull Player player) {
        return PlayerManager.getPlayer(player);
    }

}
