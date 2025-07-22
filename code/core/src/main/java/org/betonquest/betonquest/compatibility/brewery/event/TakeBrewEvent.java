package org.betonquest.betonquest.compatibility.brewery.event;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Event to take a certain amount of brews from a player.
 */
public class TakeBrewEvent implements OnlineEvent {
    /**
     * The amount of brews to take.
     */
    private final Variable<Number> countVar;

    /**
     * The name of the brew to take.
     */
    private final Variable<String> nameVar;

    /**
     * Create a new Take Brew Event.
     *
     * @param countVar The amount of brews to take.
     * @param nameVar  The name of the brew to take.
     */
    public TakeBrewEvent(final Variable<Number> countVar, final Variable<String> nameVar) {
        this.countVar = countVar;
        this.nameVar = nameVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int count = countVar.getValue(profile).intValue();
        final String name = nameVar.getValue(profile).replace("_", " ");

        final BRecipe recipe = BreweryUtils.getRecipeOrThrow(name);

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
}
