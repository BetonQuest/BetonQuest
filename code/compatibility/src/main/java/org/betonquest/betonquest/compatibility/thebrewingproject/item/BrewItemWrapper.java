package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.api.recipe.Recipe;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Brew item wrapper for the brewing project.
 *
 * @param recipeNameArgument  Recipe name argument.
 * @param brewQualityArgument Brew quality argument
 * @param api                 The api
 */
public record BrewItemWrapper(Argument<String> recipeNameArgument, Argument<BrewQuality> brewQualityArgument,
                              TheBrewingProjectApi api) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final String recipeName = recipeNameArgument.getValue(profile);
        final BrewQuality brewQuality = brewQualityArgument.getValue(profile);
        final Recipe<ItemStack> recipe = api.getRecipeRegistry().getRecipe(recipeName)
                .orElseThrow(() -> new QuestException(String.format("Unknown recipe: %s", recipeName)));
        return new BrewItem(recipe, recipeName, brewQuality, api.getBrewManager());
    }
}
