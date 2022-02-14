package ru.civwars.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import ru.civwars.util.JsonUtils;

public class MaterialTypeAdapter implements JsonSerializer<Material>, JsonDeserializer<Material> {

    @Override
    public JsonElement serialize(Material item, Type type, JsonSerializationContext context) {
        Item item2 = CraftMagicNumbers.getItem(item);
        
        return new JsonPrimitive(CraftMagicNumbers.getId(item2));
    }

    @Override
    public Material deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        int itemId = JsonUtils.getInt(element, "item_id");
        Item item = Item.getById(itemId);
        Material material = item != null ? CraftMagicNumbers.getMaterial(item) : null;
        if (material == null) {
            throw new JsonSyntaxException("Unknown material '" + itemId + "'");
        }
        return material;
    }

}
