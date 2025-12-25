package org.betonquest.betonquest.compatibility.brewery.event;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Event to give a player a certain amount of brews with a specific quality.
 */
public class GiveBrewEvent implements OnlineEvent {

    /**
     * The amount of brews to give.
     */
    private final Argument<Number> amountVar;

    /**
     * The quality of the brews.
     */
    private final Argument<Number> qualityVar;

    /**
     * The name of the brew to give.
     */
    private final Argument<String> nameVar;

    /**
     * Interpretation mode for brews.
     */
    private final Argument<IdentifierType> mode;

    /**
     * Create a new Give Brew Event.
     *
     * @param amountVar  the amount of brews to give.
     * @param qualityVar the quality of the brews.
     * @param nameVar    the name of the brew to give.
     * @param mode       the interpretation mode for brews.
     */
    public GiveBrewEvent(final Argument<Number> amountVar, final Argument<Number> qualityVar, final Argument<String> nameVar, final Argument<IdentifierType> mode) {
        this.amountVar = amountVar;
        this.qualityVar = qualityVar;
        this.nameVar = nameVar;
        this.mode = mode;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int quality = qualityVar.getValue(profile).intValue();
        BreweryUtils.validateQualityOrThrow(quality);
        final String name = nameVar.getValue(profile);
        final BRecipe recipe = mode.getValue(profile).getRecipeOrThrow(name);

        final int amount = amountVar.getValue(profile).intValue();
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
