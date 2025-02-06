package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Condition to check if a player has a certain amount of a specific brew.
 */
public class HasBrewCondition implements OnlineCondition {
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
    public boolean check(final OnlineProfile profile) throws QuestException {
        final int count = countVar.getValue(profile).intValue();
        final String name = nameVar.getValue(profile).replace("_", " ");
        final BRecipe recipe = BreweryUtils.getRecipeOrThrow(name);

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
