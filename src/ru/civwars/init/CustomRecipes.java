package ru.civwars.init;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.bukkit.inventory.ItemStack;
import ru.civwars.CivLogger;
import ru.civwars.CivWars;
import ru.civwars.exception.ContentLoadException;
import ru.civwars.item.CustomItem;
import ru.civwars.item.recipe.CustomRecipe;
import ru.civwars.item.recipe.CustomShapedRecipe;
import ru.civwars.item.recipe.CustomShapelessRecipe;
import ru.civwars.util.FileHelper;
import ru.civwars.util.ItemUtils;
import ru.civwars.util.Utilities;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class CustomRecipes {

    private static CustomRecipes instance;

    public static CustomRecipes get() {
        return instance;
    }

    public static CustomRecipes init() throws ContentLoadException {
        if (instance != null) {
            return instance;
        }

        instance = new CustomRecipes();

        return instance;
    }

    private final Map<String, CustomShapedRecipe> shapedRecipes = Maps.newHashMap();
    private final Map<String, CustomShapelessRecipe> shapelessRecipes = Maps.newHashMap();

    private CustomRecipes() throws ContentLoadException {
        this.load();
    }

    private void load() throws ContentLoadException {
        this.shapedRecipes.clear();
        this.shapelessRecipes.clear();

        File folder = new File(CivWars.get().getDataFolder(), "data/custom_recipes");
        List<File> files = FileHelper.files(folder, true);

        for (File file : files) {
            try {
                JsonObject object = Utilities.PARSER.parse(FileUtils.readFileToString(file, Charsets.UTF_8)).getAsJsonObject();
                CustomRecipe recipe = Utilities.GSON.fromJson(object, CustomRecipe.class);
                String key = null;
                ItemStack[] matrix = recipe.build();
                if (recipe instanceof CustomShapedRecipe) {
                    key = CustomRecipes.getShapedRecipeKey(matrix);
                    shapedRecipes.put(key, (CustomShapedRecipe) recipe);
                } else if (recipe instanceof CustomShapelessRecipe) {
                    key = CustomRecipes.getShapelessRecipeKey(matrix);
                    shapelessRecipes.put(key, (CustomShapelessRecipe) recipe);
                } else {
                }
            } catch (Throwable thrwbl) {
                throw new ContentLoadException(file.getPath(), thrwbl);
            }
        }

        CivLogger.log(Level.INFO, "Loaded {0} Custom Recipes", this.shapedRecipes.size() + this.shapelessRecipes.size());
    }

    /**
     * @param key.
     * @return рецепт или {@code null}, если рецепт с данным идентификатором не
     * найден.
     */
    @Nullable
    public static CustomShapedRecipe getShapedRecipe(@NotNull String key) {
        return CustomRecipes.instance.shapedRecipes.get(key);
    }

    /**
     * @param key.
     * @return рецепт или {@code null}, если рецепт с данным идентификатором не
     * найден.
     */
    @Nullable
    public static CustomShapelessRecipe getShapelessRecipe(@NotNull String key) {
        return CustomRecipes.instance.shapelessRecipes.get(key);
    }

    @NotNull
    public static String getShapedRecipeKey(@NotNull ItemStack[] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            builder.append(i).append(":");

            ItemStack stack = matrix[i];
            if (ItemUtils.isEmpty(stack)) {
                builder.append("null,");
                continue;
            }

            CustomItem item = CustomItems.get().fromItemStack(stack);
            if (item != null) {
                builder.append(item.getId());
            } else {
                builder.append("mc_").append(stack.getTypeId()).append("_").append(stack.getData().getData());
            }
            builder.append(",");
        }
        return builder.toString().trim();
    }

    @NotNull
    public static String getShapelessRecipeKey(@NotNull ItemStack[] matrix) {
        Map<String, Integer> counts = Maps.newHashMap();

        for (int i = 0; i < matrix.length; i++) {
            ItemStack stack = matrix[i];
            if (ItemUtils.isEmpty(stack)) {
                continue;
            }

            String key;
            CustomItem item = CustomItems.get().fromItemStack(stack);
            if (item != null) {
                key = item.getId() + "";
            } else {
                key = stack.getTypeId() + "_" + stack.getData().getData();
            }

            int count = counts.getOrDefault(key, 0) + 1;
            counts.put(key, count);
        }

        List<String> items = Lists.newLinkedList();

        for (String item : counts.keySet()) {
            int count = counts.get(item);
            items.add(item + ":" + count);
        }

        Collections.sort(items);

        StringBuilder builder = new StringBuilder();
        for (String item : items) {
            builder.append(item).append(",");
        }
        return builder.toString().trim();
    }
}
