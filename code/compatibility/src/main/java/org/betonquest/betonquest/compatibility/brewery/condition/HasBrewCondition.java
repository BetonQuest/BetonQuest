package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Condition to check if a player has a certain amount of a specific brew.
 */
public class HasBrewCondition implements OnlineCondition {

    /**
     * The amount of brews to check.
     */
    private final Argument<Number> count;

    /**
     * The name of the brew to check.
     */
    private final Argument<String> name;

    /**
     * Interpretation mode for brews.
     */
    private final Argument<IdentifierType> mode;

    /**
     * Creates a new has Brew condition.
     *
     * @param count the amount of brews to check.
     * @param name  the name of the brew to check.
     * @param mode  the interpretation mode for brews.
     */
    public HasBrewCondition(final Argument<Number> count, final Argument<String> name, final Argument<IdentifierType> mode) {
        this.count = count;
        this.name = name;
        this.mode = mode;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final int count = this.count.getValue(profile).intValue();
        final String name = this.name.getValue(profile);
        final BRecipe recipe = mode.getValue(profile).getRecipeOrThrow(name);

        final Player player = profile.getPlayer();
        int remaining = count;

        for (final ItemStack item : player.getInventory().getContents()) {
            if (BreweryUtils.isNotValidBrewItem(item, recipe)) {
                continue;
            }

            remaining -= item.getAmount();
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
