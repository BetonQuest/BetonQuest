package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Interface for quest-events that are executed for an online profile.
 * The difference to the {@link Event} is the guaranteed availability of a player instance.
 *
 * @see Event
 */
public interface OnlinePlayerEvent {
    /**
     * Executes the event.
     *
     * @param profile the {@link OnlineProfile} the event is executed for
     * @throws QuestRuntimeException when the event execution fails
     */
    void execute(OnlineProfile profile) throws QuestRuntimeException;
}
