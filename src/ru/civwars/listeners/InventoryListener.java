package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import ru.civwars.CivWars;
import ru.civwars.listeners.inventory.*;
import ru.lib27.annotation.NotNull;

public class InventoryListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new InventoryListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public InventoryListener(@NotNull CivWars plugin) {
        super(plugin);
        
        this.registerHandler(new CraftItemHandler(plugin));
        
        this.registerHandler(new InventoryClickHandler(plugin));
        
        this.registerHandler(new PrepareItemCraftHandler(plugin));
    }
    
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        this.handle0(event);
    }
    
}
