package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Interface for quest-events that are executed for a profile. It represents the normal event as described in the
 * BetonQuest user documentation. It does not represent the "static" variant though, see {@link StaticEvent}.
 */
public interface Event {
    /**
     * Executes the event.
     *
     * @param profile the {@link Profile} the event is executed for
     * @throws QuestException when the event execution fails
     */
    void execute(Profile profile) throws QuestException;
}
