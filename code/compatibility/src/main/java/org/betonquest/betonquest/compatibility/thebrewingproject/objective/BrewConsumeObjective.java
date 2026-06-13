package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.bukkit.api.event.BrewConsumeEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.BrewUtil;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Predicate;

/**
 * Brew consume objective
 *
 * @param qualityArgument  Argument for brew quality
 * @param brewTypeArgument Argument for brew type
 * @param service          The objective service
 */
public record BrewConsumeObjective(BrewQualityArgument qualityArgument, Argument<String> brewTypeArgument,
                                   ObjectiveService service) implements Objective {

    @Override
    public ObjectiveService getService() {
        return service;
    }

    /**
     * Handle a brew consume event.
     *
     * @param event   The event to handle
     * @param profile The online profile related to the event
     * @throws QuestException If any argument is invalid
     */
    public void handle(final BrewConsumeEvent event, final OnlineProfile profile) throws QuestException {
        final Predicate<BrewQuality> quality = qualityArgument.getValue(profile);
        final String type = brewTypeArgument.getValue(profile);
        final ItemStack itemStack = event.getItem();
        final PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (BrewUtil.brewName(container).filter(type::equals).isPresent()
                && BrewUtil.quality(container).filter(quality).isPresent()
        ) {
            service.complete(profile);
        }
    }
}
