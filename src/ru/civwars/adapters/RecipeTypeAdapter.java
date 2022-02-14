package ru.civwars.adapters;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import ru.civwars.item.recipe.CustomRecipe;
import ru.civwars.item.recipe.CustomShapedRecipe;
import ru.civwars.item.recipe.CustomShapelessRecipe;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.item.RequiredItem;
import ru.lib27.annotation.NotNull;

public class RecipeTypeAdapter implements JsonSerializer<CustomRecipe>, JsonDeserializer<CustomRecipe> {

    private static final Map<String, CustomRecipe.Serializer<?>> TYPE_TO_SERIALIZER_MAP = Maps.newHashMap();
    private static final Map<Class<? extends CustomRecipe>, CustomRecipe.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();

    static {
        registerRecipe(new CustomShapedRecipe.Serializer());
        registerRecipe(new CustomShapelessRecipe.Serializer());
    }

    public static <T extends CustomRecipe> void registerRecipe(@NotNull CustomRecipe.Serializer<? extends T> serializer) {
        String type = serializer.getName();
        Class<T> clazz = (Class<T>) serializer.getRecipeClass();
        if (RecipeTypeAdapter.TYPE_TO_SERIALIZER_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Can't re-register custom recipe type " + type);
        }
        if (RecipeTypeAdapter.CLASS_TO_SERIALIZER_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException("Can't re-register custom recipe class " + clazz.getName());
        }
        RecipeTypeAdapter.TYPE_TO_SERIALIZER_MAP.put(type, serializer);
        RecipeTypeAdapter.CLASS_TO_SERIALIZER_MAP.put(clazz, serializer);
    }

    @NotNull
    public static CustomRecipe.Serializer<?> getSerializerForType(@NotNull String type) {
        CustomRecipe.Serializer<?> serializer = RecipeTypeAdapter.TYPE_TO_SERIALIZER_MAP.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown custom recipe type '" + type + "'");
        }
        return serializer;
    }

    @NotNull
    public static <T extends CustomRecipe> CustomRecipe.Serializer<T> getSerializerFor(@NotNull T function) {
        CustomRecipe.Serializer<T> serializer = (CustomRecipe.Serializer<T>) RecipeTypeAdapter.CLASS_TO_SERIALIZER_MAP.get(function.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown custom recipe " + function);
        }
        return serializer;
    }

    @Override
    public JsonElement serialize(CustomRecipe recipe, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        return object;
    }

    @Override
    public CustomRecipe deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = JsonUtils.getJsonObject(element, "custom item");
        
        String recipeType = JsonUtils.getString(object, "type");
        CustomRecipe.Serializer<?> serializer;
        try {
            serializer = RecipeTypeAdapter.getSerializerForType(recipeType);
        } catch (IllegalArgumentException ex) {
            throw new JsonSyntaxException("Unknown custom recipe type '" + recipeType + "'");
        }

        RequiredItem result = JsonUtils.deserialize(object, "result", context, RequiredItem.class);
        RequiredItem[] ingredients = JsonUtils.deserialize(object, "ingredients", context, RequiredItem[].class);

        return (CustomRecipe) serializer.deserialize(object, context, result, ingredients);
    }

}
