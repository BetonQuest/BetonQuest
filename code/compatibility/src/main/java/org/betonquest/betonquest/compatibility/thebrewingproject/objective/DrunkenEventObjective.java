package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.util.BreweryKey;
import dev.jsinco.brewery.bukkit.api.event.DrunkEventInitiateEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * The objective for triggering a specific drunken event.
 *
 * @param drunkenEventArgument An argument with a key for the drunken event
 * @param service              The objective service
 */
public record DrunkenEventObjective(Argument<String> drunkenEventArgument,
                                    ObjectiveService service) implements Objective {

    /**
     * Handle a drunk event initiate event.
     *
     * @param event         The event to handle
     * @param onlineProfile The player profile related to the event
     * @throws QuestException If any argument is invalid
     */
    public void handle(final DrunkEventInitiateEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final String drunkEvent = drunkenEventArgument.getValue(onlineProfile);
        final BreweryKey breweryKey = BreweryKey.parse(drunkEvent);
        if (event.getDrunkenEvent().key().equals(breweryKey)) {
            service.complete(onlineProfile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
