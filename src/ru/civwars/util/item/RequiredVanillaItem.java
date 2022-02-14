package ru.civwars.util.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RequiredVanillaItem extends RequiredItem {
    
    /* Необходимый предмет. */
    private final Material item;
    private final int damage;
    
    public RequiredVanillaItem(@NotNull Material item, int count, int damage) {
        super(count);
        this.item = item;
        this.damage = damage;
    }
    
    @NotNull
    @Override
    public Material getItem() {
        return this.item;
    }

    @NotNull
    @Override
    public int getMetadata() {
        return this.damage;
    }
    
    @NotNull
    @Override
    public String getDisplayName() {
        String[] split = StringUtils.split(this.item.name(), "_");
        
        StringBuilder builder = new StringBuilder();
        for(int length = split.length, i = 0;i<length;i++) {
            String str = split[i];
            builder.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase());
            if(i + 1 < length) {
                builder.append(" ");
            }
        }
        
        if(this.item.getMaxDurability() == 0) {
            if(this.damage > 0) {
                builder.append(":").append(this.damage);
            }
        }
        
        return builder.toString();
    }
    
    @NotNull
    @Override
    public ItemStack createItemStack(int count, @Nullable KPlayer player) {
        Material item = this.item != Material.AIR ? this.item : Material.STICK;
        int fixCount = Math.max(1, Math.min(item.getMaxStackSize(), count));
        int damage = this.damage;
        if(item.getMaxDurability() > 0) {
            damage = Math.max(0, Math.min(item.getMaxDurability(), this.damage));
        }
        
        ItemStack stack = new ItemStack(item, fixCount, (short) damage);
        
        return stack;
    }
    
    public static class Serializer extends RequiredItem.Serializer<RequiredVanillaItem>{

        public Serializer() {
            super("minecraft_item", RequiredVanillaItem.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull RequiredVanillaItem item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public RequiredVanillaItem deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int count) {
             return new RequiredVanillaItem(JsonUtils.getMaterial(object, "item_id"), count, JsonUtils.getIntOrDefault(object, "metadata", 0));
        }
    }
    
}
