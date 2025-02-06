package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Condition to check if a player has a certain amount of a specific brew.
 */
public class HasBrewCondition implements PlayerCondition {
    /**
     * The {@link VariableNumber} for the amount of brews to check.
     */
    private final VariableNumber countVar;

    /**
     * The {@link VariableString} for the name of the brew to check.
     */
    private final VariableString nameVar;

    /**
     * Creates a new has Brew condition.
     *
     * @param countVar the {@link VariableNumber} for the amount of brews to check.
     * @param nameVar  the {@link VariableString} for the name of the brew to check.
     */
    public HasBrewCondition(final VariableNumber countVar, final VariableString nameVar) {
        this.countVar = countVar;
        this.nameVar = nameVar;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final int count = countVar.getValue(profile).intValue();
        final BreweryUtils breweryUtils = new BreweryUtils();
        breweryUtils.validateCountOrThrow(count, "You cant check for less then 1 Brew!");

        final String name = nameVar.getValue(profile).replace("_", " ");
        final BRecipe recipe = breweryUtils.getRecipeOrThrow(name);

        final Player player = profile.getOnlineProfile().get().getPlayer();
        int remaining = count;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (!breweryUtils.isValidBrewItem(item, recipe)) {
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
