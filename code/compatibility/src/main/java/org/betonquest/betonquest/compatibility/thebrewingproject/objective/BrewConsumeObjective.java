package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.BrewManager;
import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.bukkit.api.event.BrewConsumeEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

/**
 * Brew consume objective.
 *
 * @param service          the objective service
 * @param brewManager      the brew manager provided by TheBrewingProject
 * @param qualityArgument  argument for brew quality
 * @param brewTypeArgument argument for brew type
 */
public record BrewConsumeObjective(ObjectiveService service, BrewManager<ItemStack> brewManager,
                                   BrewQualityArgument qualityArgument,
                                   Argument<String> brewTypeArgument) implements Objective {

    /**
     * Handle a brew consume event.
     *
     * @param event   the event to handle
     * @param profile the online profile related to the event
     * @throws QuestException if any argument is invalid
     */
    public void handle(final BrewConsumeEvent event, final OnlineProfile profile) throws QuestException {
        final Predicate<BrewQuality> quality = qualityArgument.resolve(profile);
        final String type = brewTypeArgument.getValue(profile);
        final ItemStack itemStack = event.getItem();
        if (brewManager.brewName(itemStack).filter(type::equals).isPresent()
                && brewManager.brewQuality(itemStack).filter(quality).isPresent()
        ) {
            service.complete(profile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
