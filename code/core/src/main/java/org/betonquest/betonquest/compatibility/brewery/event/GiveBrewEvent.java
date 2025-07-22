package org.betonquest.betonquest.compatibility.brewery.event;

import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;
import org.betonquest.betonquest.instruction.variable.Variable;
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
    private final Variable<Number> amountVar;

    /**
     * The quality of the brews.
     */
    private final Variable<Number> qualityVar;

    /**
     * The name of the brew to give.
     */
    private final Variable<String> nameVar;

    /**
     * Create a new Give Brew Event.
     *
     * @param amountVar  the amount of brews to give.
     * @param qualityVar the quality of the brews.
     * @param nameVar    the name of the brew to give.
     */
    public GiveBrewEvent(final Variable<Number> amountVar, final Variable<Number> qualityVar, final Variable<String> nameVar) {
        this.amountVar = amountVar;
        this.qualityVar = qualityVar;
        this.nameVar = nameVar;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final int quality = qualityVar.getValue(profile).intValue();
        BreweryUtils.validateQualityOrThrow(quality);

        final String name = nameVar.getValue(profile).replace("_", " ");
        final BRecipe recipe = BreweryUtils.getRecipeOrThrow(name);

        final int amount = amountVar.getValue(profile).intValue();
        final ItemStack[] brews = IntStream.range(0, amount)
                .mapToObj(i -> recipe.create(quality))
                .toArray(ItemStack[]::new);

        final Collection<ItemStack> remaining = player.getInventory().addItem(brews).values();
        remaining.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }
}
