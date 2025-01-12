package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event with its chance.
 *
 * @param eventID the event to be executed
 * @param chance  the chance of the event
 */
public record RandomEvent(EventID eventID, VariableNumber chance) {

    /**
     * Resolves the chance of the event.
     *
     * @param profile the profile of the player
     * @return the resolved chance for the event
     * @throws QuestException if there is an error while resolving the chance
     */
    /* default */ ResolvedRandomEvent resolveFor(@Nullable final Profile profile) throws QuestException {
        return new ResolvedRandomEvent(eventID, chance.getValue(profile).doubleValue());
    }
}
