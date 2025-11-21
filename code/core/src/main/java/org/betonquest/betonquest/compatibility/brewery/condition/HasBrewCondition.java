package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
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
    private final Variable<Number> countVar;

    /**
     * The name of the brew to check.
     */
    private final Variable<String> nameVar;

    /**
     * Interpretation mode for brews.
     */
    private final Variable<IdentifierType> mode;

    /**
     * Creates a new has Brew condition.
     *
     * @param countVar the amount of brews to check.
     * @param nameVar  the name of the brew to check.
     * @param mode     the interpretation mode for brews.
     */
    public HasBrewCondition(final Variable<Number> countVar, final Variable<String> nameVar, final Variable<IdentifierType> mode) {
        this.countVar = countVar;
        this.nameVar = nameVar;
        this.mode = mode;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final int count = countVar.getValue(profile).intValue();
        final String name = nameVar.getValue(profile);
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
}
