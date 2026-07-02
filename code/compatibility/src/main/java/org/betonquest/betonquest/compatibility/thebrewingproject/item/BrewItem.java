package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.api.recipe.Recipe;
import dev.jsinco.brewery.api.recipe.RecipeMatcherResult;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A TheBrewingProject brew item.
 *
 * @param recipe      the recipe this brew item completes
 * @param recipeName  the name of the recipe
 * @param quality     the brew quality, or null if not specified
 * @param brewManager the brew manager provided by TheBrewingProject
 */
public record BrewItem(Recipe<ItemStack> recipe, String recipeName, @Nullable BrewQuality quality,
                       BrewManager<ItemStack> brewManager) implements QuestItem {

    @Override
    public Component getName() {
        return recipe.getRecipeResult(quality == null ? BrewQuality.EXCELLENT : quality).displayName();
    }

    @Override
    public List<Component> getLore() {
        return recipe.getRecipeResult(quality == null ? BrewQuality.EXCELLENT : quality).staticLore();
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
        final RecipeMatcherResult<ItemStack> match = brewManager.matcher()
                .matchAgainstOnly(recipe)
                .disallowStepVariations()
                .build()
                .match(brewManager.createBrew(recipe.getSteps()));
        final ItemStack output;
        if (quality == null) {
            output = match.toItem(new Brew.State.Seal(null));
        } else {
            output = match.toItem(new Brew.State.Seal(null), quality);
        }
        output.setAmount(stackSize);
        return output;
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        if (item == null) {
            return false;
        }
        if (quality != null && brewManager.brewQuality(item).filter(quality::equals).isEmpty()) {
            return false;
        }
        return brewManager.brewName(item).filter(recipeName::equals).isPresent();
    }
}
