package org.betonquest.betonquest.compatibility.brewery.event;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Event to give a player a certain amount of brews with a specific quality.
 */
public class GiveBrewEvent implements Event {
    /**
     * The {@link VariableNumber} for the amount of brews to give.
     */
    private final VariableNumber amountVar;

    /**
     * The {@link VariableNumber} for the quality of the brews.
     */
    private final VariableNumber qualityVar;

    /**
     * The {@link VariableString} for the name of the brew to give.
     */
    private final VariableString nameVar;

    /**
     * Create a new Give Brew Event.
     *
     * @param amountVar  the {@link VariableNumber} for the amount of brews to give.
     * @param qualityVar the {@link VariableNumber} for the quality of the brews.
     * @param nameVar    the {@link VariableString} for the name of the brew to give.
     */
    public GiveBrewEvent(final VariableNumber amountVar, final VariableNumber qualityVar, final VariableString nameVar) {
        this.amountVar = amountVar;
        this.qualityVar = qualityVar;
        this.nameVar = nameVar;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final BreweryUtils breweryUtils = new BreweryUtils();

        final int quality = qualityVar.getValue(profile).intValue();
        breweryUtils.validateQualityOrThrow(quality);

        final String name = nameVar.getValue(profile).replace("_", " ");
        final BRecipe recipe = breweryUtils.getRecipeOrThrow(name);

        final int amount = amountVar.getValue(profile).intValue();
        final ItemStack[] brews = IntStream.range(0, amount)
                .mapToObj(i -> recipe.create(quality))
                .toArray(ItemStack[]::new);

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();
        remaining.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }
}
