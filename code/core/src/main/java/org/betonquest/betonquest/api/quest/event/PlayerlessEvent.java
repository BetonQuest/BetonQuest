package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for playerless quest-events.
 * It represents the playerless event as described in the BetonQuest user documentation.
 * They may act on all players, only online player or even no player at all; this is an implementation detail.
 * For the normal event variant see {@link PlayerEvent}.
 */
@FunctionalInterface
public interface PlayerlessEvent extends PrimaryThreadEnforceable {

    /**
     * Executes the playerless event.
     *
     * @throws QuestException when the event execution fails
     */
    void execute() throws QuestException;
}
