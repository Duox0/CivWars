package ru.civwars.item.recipe;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.bukkit.inventory.ItemStack;
import ru.civwars.util.item.RequiredItem;
import ru.lib27.annotation.NotNull;

public abstract class CustomRecipe {

    protected final RequiredItem result;
    protected final RequiredItem[] ingredients;

    public CustomRecipe(@NotNull RequiredItem result, @NotNull RequiredItem[] ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    /**
     * @return результат рецепта.
     */
    @NotNull
    public RequiredItem getResult() {
        return this.result;
    }

    @NotNull
    public abstract ItemStack[] build();

    public abstract static class Serializer<T extends CustomRecipe> {

        private final String name;
        private final Class<T> clazz;

        protected Serializer(@NotNull String name, @NotNull Class<T> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        @NotNull
        public final String getName() {
            return this.name;
        }

        @NotNull
        public final Class<T> getRecipeClass() {
            return this.clazz;
        }

        public abstract void serialize(@NotNull JsonObject object, @NotNull T item, @NotNull JsonSerializationContext context);

        @NotNull
        public abstract T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, @NotNull RequiredItem result, @NotNull RequiredItem[] ingredients);
    }

}
