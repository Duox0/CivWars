package ru.civwars.listeners.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import ru.civwars.CivWars;
import ru.civwars.init.CustomRecipes;
import ru.civwars.init.CustomItems;
import ru.civwars.item.CustomItem;
import ru.civwars.item.recipe.CustomRecipe;
import ru.civwars.listeners.BasicHandler;
import ru.civwars.util.ItemUtils;
import ru.lib27.annotation.NotNull;

public class PrepareItemCraftHandler extends BasicHandler<PrepareItemCraftEvent> {

    public PrepareItemCraftHandler(@NotNull CivWars civcraft) {
        super(civcraft, PrepareItemCraftEvent.class);
    }

    @Override
    protected void handle(@NotNull PrepareItemCraftEvent event) {
        boolean isRepair = event.isRepair();
        ItemStack result = event.getInventory().getResult();
        ItemStack[] matrix = event.getInventory().getMatrix();

        CustomRecipe recipe;
        if (event.getRecipe() instanceof ShapedRecipe) {
            String key = CustomRecipes.getShapedRecipeKey(matrix);
            recipe = CustomRecipes.getShapedRecipe(key);
        } else if (event.getRecipe() instanceof ShapelessRecipe) {
            String key = CustomRecipes.getShapelessRecipeKey(matrix);
            recipe = CustomRecipes.getShapelessRecipe(key);
        } else {
            return;
        }
        
        CustomItem item = !ItemUtils.isEmpty(result) ? CustomItems.get().fromItemStack(result) : null;
        if (recipe == null) {
            if (this.matrixContainsCustom(matrix)) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
            }
        } else if (item != null) {
            //event.getInventory().setResult(item.createItemStack());
        }
    }

    private boolean matrixContainsCustom(@NotNull ItemStack[] matrix) {
        for (ItemStack stack : matrix) {
            if (CustomItems.get().fromItemStack(stack) != null) {
                return true;
            }
        }
        return false;
    }
}
