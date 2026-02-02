package org.betonquest.betonquest.compatibility.brewery.action;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Action to take a certain amount of brews from a player.
 */
public class TakeBrewAction implements OnlineAction {

    /**
     * The amount of brews to take.
     */
    private final Argument<Number> count;

    /**
     * The name of the brew to take.
     */
    private final Argument<String> name;

    /**
     * Interpretation mode for brews.
     */
    private final Argument<IdentifierType> mode;

    /**
     * Create a new Take Brew Action.
     *
     * @param count The amount of brews to take.
     * @param name  The name of the brew to take.
     * @param mode  the interpretation mode for brews.
     */
    public TakeBrewAction(final Argument<Number> count, final Argument<String> name, final Argument<IdentifierType> mode) {
        this.count = count;
        this.name = name;
        this.mode = mode;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int count = this.count.getValue(profile).intValue();
        final String name = this.name.getValue(profile);
        final BRecipe recipe = mode.getValue(profile).getRecipeOrThrow(name);

        int remaining = count;
        final PlayerInventory inventory = player.getInventory();
        final int invSize = inventory.getSize();

        for (int i = 0; i < invSize && remaining > 0; i++) {
            final ItemStack item = inventory.getItem(i);
            if (BreweryUtils.isNotValidBrewItem(item, recipe)) {
                continue;
            }

            final int itemAmount = item.getAmount();
            if (itemAmount - remaining <= 0) {
                remaining -= itemAmount;
                inventory.setItem(i, null);
            } else {
                item.setAmount(itemAmount - remaining);
                remaining = 0;
            }
        }
        player.updateInventory();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
