package ru.civwars.item.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import ru.civwars.util.item.RequiredItem;
import ru.lib27.annotation.NotNull;

public class CustomShapelessRecipe extends CustomRecipe {

    public CustomShapelessRecipe(@NotNull RequiredItem result, @NotNull RequiredItem[] ingredients) {
        super(result, ingredients);
    }

    @NotNull
    @Override
    public ItemStack[] build() {
        ItemStack result = this.result.createItemStack(1);

        ShapelessRecipe recipe = new ShapelessRecipe(result);
        ItemStack[] matrix = new ItemStack[9];
        int matrixIndex = 0;

        for (RequiredItem ingredient : this.ingredients) {
            recipe.addIngredient(ingredient.getCount(), new MaterialData(ingredient.getItem().getId(), (byte) ingredient.getMetadata()));

            for (int i = 0; i < ingredient.getCount(); i++) {
                if (matrixIndex > 9) {
                    break;
                }

                matrix[matrixIndex] = ingredient.createItemStack(ingredient.getCount());
                matrixIndex++;
            }
        }
        
        Bukkit.getServer().addRecipe(recipe);
        return matrix;
    }

    public static class Serializer extends CustomRecipe.Serializer<CustomShapelessRecipe> {

        public Serializer() {
            super("shapeless", CustomShapelessRecipe.class);
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull CustomShapelessRecipe recipe, @NotNull JsonSerializationContext context) {
        }

        @NotNull
        @Override
        public CustomShapelessRecipe deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull RequiredItem result, @NotNull RequiredItem[] ingredients) {
            return new CustomShapelessRecipe(result, ingredients);
        }
    }
}
