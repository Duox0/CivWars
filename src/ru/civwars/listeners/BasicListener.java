package ru.civwars.listeners;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import ru.civwars.CivWars;
import ru.lib27.annotation.NotNull;

public abstract class BasicListener implements Listener {

    protected final CivWars plugin;

    private final Map<Class<? extends Event>, BasicHandler> handlers = Maps.newHashMap();
    
    public BasicListener(@NotNull CivWars plugin) {
        this.plugin = plugin;
    }
    
    protected void registerHandler(@NotNull BasicHandler handler) {
        this.handlers.put(handler.getEventClass(), handler);
    }
    
    protected void handle0(@NotNull Event event) {
        BasicHandler handler = this.handlers.get(event.getClass());
        if(handler != null) {
            handler.handle(event);
        }
    }

}
