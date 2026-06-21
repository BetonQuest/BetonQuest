package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.api.recipe.Recipe;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The brewing project brew item.
 *
 * @param recipe     The recipe this brew item completes
 * @param recipeName The name of the recipe
 * @param quality    The brew quality
 * @param api        The api needed to craft brew
 */
public record BrewItem(Recipe<ItemStack> recipe, String recipeName, BrewQuality quality,
                       BrewManager<ItemStack> api) implements QuestItem {

    @Override
    public Component getName() {
        return recipe.getRecipeResult(quality).displayName();
    }

    @Override
    public List<Component> getLore() {
        return recipe.getRecipeResult(quality).staticLore();
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
        final ItemStack output = api.matcher()
                .matchAgainstOnly(recipe)
                .disallowStepVariations()
                .build()
                .match(api.createBrew(recipe.getSteps()))
                .toItem(new Brew.State.Seal(null));
        output.setAmount(stackSize);
        return output;
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        return item != null && api.brewName(item)
                .filter(recipeName::equals)
                .isPresent();
    }
}
