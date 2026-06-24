package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.api.recipe.Recipe;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Brew item wrapper for the brewing project.
 *
 * @param recipeNameArgument  a recipe name argument
 * @param brewQualityArgument a brew quality argument
 * @param api                 the TheBrewingProject API
 */
public record BrewItemWrapper(Argument<String> recipeNameArgument, FlagArgument<BrewQuality> brewQualityArgument,
                              TheBrewingProjectApi api) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final String recipeName = recipeNameArgument.getValue(profile);
        final BrewQuality brewQuality = brewQualityArgument.getValue(profile).orElse(null);
        final Recipe<ItemStack> recipe = api.getRecipeRegistry().getRecipe(recipeName)
                .orElseThrow(() -> new QuestException("Unknown recipe: %s".formatted(recipeName)));
        return new BrewItem(recipe, recipeName, brewQuality, api.getBrewManager());
    }
}
