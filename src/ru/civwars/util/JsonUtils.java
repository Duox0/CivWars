package ru.civwars.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import net.minecraft.server.v1_12_R1.Item;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class JsonUtils {

    public static boolean isNumber(@NotNull JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
    
    public static String getString(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a String, was" + toString(element));
    }

    public static String getString(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getString(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a String");
    }

    public static String getStringOrDefault(@NotNull JsonObject object, @NotNull String key, String value) {
        if (object.has(key)) {
            return getString(object.get(key), key);
        }
        return value;
    }

    public static byte getByte(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Byte, was" + toString(element));
    }

    public static byte getByte(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getByte(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Byte");
    }

    public static byte getByteOrDefault(@NotNull JsonObject object, @NotNull String key, byte value) {
        if (object.has(key)) {
            return getByte(object.get(key), key);
        }
        return value;
    }

    public static short getShort(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsShort();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Short, was" + toString(element));
    }

    public static short getShort(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getShort(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Short");
    }

    public static short getShortOrDefault(@NotNull JsonObject object, @NotNull String key, short value) {
        if (object.has(key)) {
            return getShort(object.get(key), key);
        }
        return value;
    }

    public static int getInt(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Int, was " + toString(element));
    }

    public static int getInt(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getInt(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Int");
    }

    public static int getIntOrDefault(@NotNull JsonObject object, @NotNull String key, int value) {
        if (object.has(key)) {
            return getInt(object.get(key), key);
        }
        return value;
    }

    public static long getLong(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Long, was " + toString(element));
    }

    public static long getLong(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getLong(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Long");
    }

    public static long getLongOrDefault(@NotNull JsonObject object, @NotNull String key, long value) {
        if (object.has(key)) {
            return getLong(object.get(key), key);
        }
        return value;
    }

    public static boolean getBoolean(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive()) {
            return element.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Boolean, was " + toString(element));
    }

    public static boolean getBoolean(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getBoolean(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Boolean");
    }

    public static boolean getBooleanOrDefault(@NotNull JsonObject object, @NotNull String key, boolean value) {
        if (object.has(key)) {
            return getBoolean(object.get(key), key);
        }
        return value;
    }

    public static float getFloat(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Float, was " + toString(element));
    }

    public static float getFloat(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getFloat(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Float");
    }

    public static float getFloatOrDefault(@NotNull JsonObject object, @NotNull String key, float value) {
        if (object.has(key)) {
            return getFloat(object.get(key), key);
        }
        return value;
    }

    public static double getDouble(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsDouble();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Double, was " + toString(element));
    }

    public static double getDouble(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getDouble(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Double");
    }

    public static double getDoubleOrDefault(@NotNull JsonObject object, @NotNull String key, double value) {
        if (object.has(key)) {
            return getDouble(object.get(key), key);
        }
        return value;
    }

    public static JsonObject getJsonObject(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a JsonObject, was " + toString(element));
    }

    public static JsonObject getJsonObject(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getJsonObject(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
    }

    public static JsonObject getJsonObjectOrDefault(@NotNull JsonObject object, @NotNull String key, JsonObject value) {
        if (object.has(key)) {
            return getJsonObject(object.get(key), key);
        }
        return value;
    }

    public static JsonArray getJsonArray(@NotNull JsonElement element, @NotNull String key) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a JsonArray, was " + toString(element));
    }

    public static JsonArray getJsonArray(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getJsonArray(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonArray");
    }

    public static JsonArray getJsonArrayOrDefault(@NotNull JsonObject object, @NotNull String key, JsonArray value) {
        if (object.has(key)) {
            return getJsonArray(object.get(key), key);
        }
        return value;
    }

    public static <T> T deserialize(JsonElement element, @NotNull String key, @NotNull JsonDeserializationContext context, @NotNull Class<? extends T> type) {
        if (element != null) {
            return (T) context.deserialize(element, (Type) type);
        }
        throw new JsonSyntaxException("Missing " + key);
    }

    public static <T> T deserialize(@NotNull JsonObject object, @NotNull String key, @NotNull JsonDeserializationContext context, @NotNull Class<? extends T> type) {
        if (object.has(key)) {
            return deserialize(object.get(key), key, context, type);
        }
        throw new JsonSyntaxException("Missing " + key);
    }

    public static <T> T deserializeOrDefault(@NotNull JsonObject object, @NotNull String key, @Nullable T value, @NotNull JsonDeserializationContext context, @NotNull Class<? extends T> type) {
        if (object.has(key)) {
            return deserialize(object.get(key), key, context, type);
        }
        return value;
    }
    
    public static Item getItem(@NotNull JsonElement element, @NotNull String key) {
        if (!element.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected " + key + " to be an Item, was " + toString(element));
        }
        String asString = element.getAsString();
        Item item = Item.b(asString);
        if (item == null) {
            throw new JsonSyntaxException("Expected " + key + " to be an Item, was unknown string '" + asString + "'");
        }
        return item;
    }
    
    public static Item getItem(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getItem(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an Item");
    }
    
    public static Material getMaterial(@NotNull JsonElement element, @NotNull String key) {
        if (!element.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected " + key + " to be an Material, was " + toString(element));
        }
        String asString = element.getAsString();
        Item item = Item.b(asString);
        Material material = item != null ? CraftMagicNumbers.getMaterial(item) : null;
        if (material == null) {
            throw new JsonSyntaxException("Expected " + key + " to be an Material, was unknown string '" + asString + "'");
        }
        return material;
    }
    
    public static Material getMaterial(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getMaterial(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an Material");
    }
    
    public static Enchantment getEnchantment(@NotNull JsonElement element, @NotNull String key) {
        if (!element.isJsonPrimitive()) {
            throw new JsonSyntaxException("Expected " + key + " to be an Enchantment, was " + toString(element));
        }
        String asString = element.getAsString();
        Enchantment item = Enchantment.getByName(asString.toUpperCase());
        if (item == null) {
            throw new JsonSyntaxException("Expected " + key + " to be an Enchantment, was unknown string '" + asString + "'");
        }
        return item;
    }
    
    public static Enchantment getEnchantment(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getEnchantment(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an Enchantment");
    }
    
    public static CustomItem getCustomItem(@NotNull JsonElement element, @NotNull String key) {
        if (!(element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())) {
            throw new JsonSyntaxException("Expected " + key + " to be an CustomItem, was " + toString(element));
        }
        int asInt = element.getAsInt();
        CustomItem item = CustomItems.get(asInt);
        if (item == null) {
            throw new JsonSyntaxException("Expected " + key + " to be an CustomItem, was unknown string '" + asInt + "'");
        }
        return item;
    }
    
    public static CustomItem getCustomItem(@NotNull JsonObject object, @NotNull String key) {
        if (object.has(key)) {
            return getCustomItem(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an CustomItem");
    }
    
    public static String toString(JsonElement element) {
        String str = StringUtils.abbreviateMiddle(String.valueOf(element), "...", 10);
        if (element == null) {
            return "null (missing)";
        } else if (element.isJsonNull()) {
            return "null (json)";
        } else if (element.isJsonArray()) {
            return "an array (" + str + ")";
        } else if (element.isJsonObject()) {
            return "an object (" + str + ")";
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive prmtv = element.getAsJsonPrimitive();
            if (prmtv.isNumber()) {
                return "a number (" + str + ")";
            } else if (prmtv.isBoolean()) {
                return "a boolean (" + str + ")";
            }
        }
        return str;
    }
}
