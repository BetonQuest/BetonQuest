package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Interface for quest-events that act on a player. It represents the normal event as described in the BetonQuest user
 * documentation. It does not represent the "static" variant though, see {@link StaticEvent}.
 */
public interface Event {
    /**
     * Executes the event.
     *
     * @param playerId player the event is done for
     * @throws QuestRuntimeException when the event execution fails
     */
    void execute(String playerId) throws QuestRuntimeException;
}
