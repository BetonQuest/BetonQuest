package org.betonquest.betonquest.compatibility.brewery.event;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Event to give a player a certain amount of brews with a specific quality.
 */
public class GiveBrewAction implements OnlineAction {

    /**
     * The amount of brews to give.
     */
    private final Argument<Number> amount;

    /**
     * The quality of the brews.
     */
    private final Argument<Number> quality;

    /**
     * The name of the brew to give.
     */
    private final Argument<String> name;

    /**
     * Interpretation mode for brews.
     */
    private final Argument<IdentifierType> mode;

    /**
     * Create a new Give Brew Event.
     *
     * @param amount  the amount of brews to give.
     * @param quality the quality of the brews.
     * @param name    the name of the brew to give.
     * @param mode    the interpretation mode for brews.
     */
    public GiveBrewAction(final Argument<Number> amount, final Argument<Number> quality, final Argument<String> name, final Argument<IdentifierType> mode) {
        this.amount = amount;
        this.quality = quality;
        this.name = name;
        this.mode = mode;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int quality = this.quality.getValue(profile).intValue();
        BreweryUtils.validateQualityOrThrow(quality);
        final String name = this.name.getValue(profile);
        final BRecipe recipe = mode.getValue(profile).getRecipeOrThrow(name);

        final int amount = this.amount.getValue(profile).intValue();
        final ItemStack[] brews = IntStream.range(0, amount)
                .mapToObj(i -> recipe.create(quality))
                .toArray(ItemStack[]::new);

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();
        remaining.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
