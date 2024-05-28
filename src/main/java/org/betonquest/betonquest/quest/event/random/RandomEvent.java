package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;

/**
 * Represents an event with its chance.
 *
 * @param eventID the event to be executed
 * @param chance  the chance of the event
 */
record RandomEvent(EventID eventID, VariableNumber chance) {

    /**
     * Resolves the chance of the event.
     *
     * @param profile the profile of the player
     * @return the resolved chance for the event
     * @throws QuestRuntimeException if there is an error while resolving the chance
     */
    /* default */ ResolvedRandomEvent resolveFor(final Profile profile) throws QuestRuntimeException {
        return new ResolvedRandomEvent(eventID, chance.getDouble(profile));
    }
}
