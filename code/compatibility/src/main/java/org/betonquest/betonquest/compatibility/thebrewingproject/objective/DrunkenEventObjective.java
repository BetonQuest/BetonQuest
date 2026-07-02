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
 * @param service              the objective service
 * @param drunkenEventArgument an argument with a key for the drunken event
 */
public record DrunkenEventObjective(ObjectiveService service,
                                    Argument<String> drunkenEventArgument) implements Objective {

    /**
     * Handle a drunk event initiate event.
     *
     * @param event         the event to handle
     * @param onlineProfile the player profile related to the event
     * @throws QuestException if any argument is invalid
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
