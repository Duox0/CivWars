package ru.civwars.util.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.civwars.i18n.I18n;
import ru.civwars.item.CustomItem;
import ru.civwars.entity.player.KPlayer;
import ru.civwars.util.JsonUtils;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class RequiredCustomItem extends RequiredItem {
    
    /* Необходимый предмет. */
    private final CustomItem item;
    
    public RequiredCustomItem(@NotNull CustomItem item, int count) {
        super(count);
        this.item = item;
    }
    
    @NotNull
    @Override
    public Material getItem() {
        return this.item.getItem();
    }

    @NotNull
    @Override
    public int getMetadata() {
        return this.item.getMetadata();
    }
    
    @NotNull
    @Override
    public String getDisplayName() {
        return I18n.tl("item_" + this.item.getId());
    }
    
    @NotNull
    @Override
    public ItemStack createItemStack(int count, @Nullable KPlayer player) {
        return this.item.createItemStack(count);
    }
    
    public static class Serializer extends RequiredItem.Serializer<RequiredCustomItem>{

        public Serializer() {
            super("civcraft_item", RequiredCustomItem.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull RequiredCustomItem item, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public RequiredCustomItem deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, int count) {
             return new RequiredCustomItem(JsonUtils.getCustomItem(object, "item_id"), count);
        }
    }
}
