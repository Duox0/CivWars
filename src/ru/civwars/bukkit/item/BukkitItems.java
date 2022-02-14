package ru.civwars.bukkit.item;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.Material;
import ru.civwars.bukkit.inventory.BukkitItemSlot;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class BukkitItems {
    
    private static final Map<Material, BukkitItem> ITEMS = Maps.newHashMap();
    
    static {
        registerItem(new BukkitItemArmor(Material.LEATHER_HELMET, BukkitItemSlot.HEAD));
        registerItem(new BukkitItemArmor(Material.LEATHER_CHESTPLATE, BukkitItemSlot.CHEST));
        registerItem(new BukkitItemArmor(Material.LEATHER_LEGGINGS, BukkitItemSlot.LEGS));
        registerItem(new BukkitItemArmor(Material.LEATHER_BOOTS, BukkitItemSlot.FEET));
        
        registerItem(new BukkitItemArmor(Material.CHAINMAIL_HELMET, BukkitItemSlot.HEAD));
        registerItem(new BukkitItemArmor(Material.CHAINMAIL_CHESTPLATE, BukkitItemSlot.CHEST));
        registerItem(new BukkitItemArmor(Material.CHAINMAIL_LEGGINGS, BukkitItemSlot.LEGS));
        registerItem(new BukkitItemArmor(Material.CHAINMAIL_BOOTS, BukkitItemSlot.FEET));
        
        registerItem(new BukkitItemArmor(Material.IRON_HELMET, BukkitItemSlot.HEAD));
        registerItem(new BukkitItemArmor(Material.IRON_CHESTPLATE, BukkitItemSlot.CHEST));
        registerItem(new BukkitItemArmor(Material.IRON_LEGGINGS, BukkitItemSlot.LEGS));
        registerItem(new BukkitItemArmor(Material.IRON_BOOTS, BukkitItemSlot.FEET));
        
        registerItem(new BukkitItemArmor(Material.GOLD_HELMET, BukkitItemSlot.HEAD));
        registerItem(new BukkitItemArmor(Material.GOLD_CHESTPLATE, BukkitItemSlot.CHEST));
        registerItem(new BukkitItemArmor(Material.GOLD_LEGGINGS, BukkitItemSlot.LEGS));
        registerItem(new BukkitItemArmor(Material.GOLD_BOOTS, BukkitItemSlot.FEET));
        
        registerItem(new BukkitItemArmor(Material.DIAMOND_HELMET, BukkitItemSlot.HEAD));
        registerItem(new BukkitItemArmor(Material.DIAMOND_CHESTPLATE, BukkitItemSlot.CHEST));
        registerItem(new BukkitItemArmor(Material.DIAMOND_LEGGINGS, BukkitItemSlot.LEGS));
        registerItem(new BukkitItemArmor(Material.DIAMOND_BOOTS, BukkitItemSlot.FEET));
    }
    
    public static <T extends BukkitItem> void registerItem(@NotNull T item) {
        Material type = item.getItem();
        if (BukkitItems.ITEMS.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register bukkit item type " + type);
        }
        BukkitItems.ITEMS.put(type, item);
    }
    
    @Nullable
    public static BukkitItem getBukkitItem(@NotNull Material type) {
        return ITEMS.get(type);
    }
    
}
