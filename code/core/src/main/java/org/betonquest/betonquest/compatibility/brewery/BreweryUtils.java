package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.Brew;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for Brewery.
 */
public final class BreweryUtils {

    private BreweryUtils() {
    }

    /**
     * Check if an item is a valid brew item for a specific recipe.
     *
     * @param item   the {@link ItemStack} to check.
     * @param recipe the {@link BRecipe} to check against.
     * @return {@code true} if the item is a valid brew item for the recipe, {@code false} otherwise.
     */
    public static boolean isNotValidBrewItem(@Nullable final ItemStack item, final BRecipe recipe) {
        if (item == null) {
            return true;
        }
        final Brew brewItem = Brew.get(item);
        return brewItem == null || !recipe.equals(brewItem.getCurrentRecipe());
    }

    /**
     * Get a brewing recipe by name or throw a {@link QuestException} if it does not exist.
     *
     * @param name the name of the brewing recipe.
     * @return the {@link BRecipe} with the given name.
     * @throws QuestException if there is no brewing recipe with the given name.
     */
    public static BRecipe getRecipeOrThrow(final String name) throws QuestException {
        final BRecipe recipe = BRecipe.get(name);
        if (recipe == null) {
            throw new QuestException("There is no brewing recipe with the name " + name + "!");
        }
        return recipe;
    }

    /**
     * Validate a quality and throw a {@link QuestException} if it is not between 1 and 10.
     *
     * @param quality the quality to validate.
     * @throws QuestException if the quality is not between 1 and 10.
     */
    public static void validateQualityOrThrow(final int quality) throws QuestException {
        if (quality <= 0 || quality > 10) {
            throw new QuestException("Drunk quality can only be between 1 and 10!");
        }
    }
}
