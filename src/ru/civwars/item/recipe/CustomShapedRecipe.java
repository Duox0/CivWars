package ru.civwars.item.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import ru.civwars.util.JsonUtils;
import ru.civwars.util.item.RequiredItem;
import ru.lib27.annotation.NotNull;

public class CustomShapedRecipe extends CustomRecipe {

    private final String[] shape;

    public CustomShapedRecipe(@NotNull RequiredItem result, @NotNull RequiredItem[] ingredients, @NotNull String[] shape) {
        super(result, ingredients);
        this.shape = shape;
    }

    @NotNull
    @Override
    public ItemStack[] build() {
        ItemStack result = this.result.createItemStack(1);

        ShapedRecipe recipe = new ShapedRecipe(result);
        ItemStack[] matrix = new ItemStack[9];

        recipe.shape(this.shape[0], this.shape[1], this.shape[2]);

        for (int length = this.ingredients.length, i = 0; i < length && i < 9; i++) {
            char letter = String.valueOf(i).charAt(0);
            RequiredItem ingredient = this.ingredients[i];
            recipe.setIngredient(letter, new MaterialData(ingredient.getItem().getId(), (byte) ingredient.getMetadata()));

            int j = 0;
            for (String row : this.shape) {
                for (int c = 0; c < row.length(); c++) {
                    if (row.charAt(c) == letter) {
                        matrix[j] = ingredient.createItemStack(1);
                    }
                    j++;
                }
            }
        }

        Bukkit.getServer().addRecipe(recipe);

        return matrix;
    }

    public static class Serializer extends CustomRecipe.Serializer<CustomShapedRecipe> {

        public Serializer() {
            super("shaped", CustomShapedRecipe.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull CustomShapedRecipe recipe, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public CustomShapedRecipe deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull RequiredItem result, @NotNull RequiredItem[] ingredients) {
            return new CustomShapedRecipe(result, ingredients, JsonUtils.deserialize(object, "shape", context, String[].class));
        }
    }
}
