package org.betonquest.betonquest.api.quest.event.online;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Quest event that needs an online profile to function correctly.
 */
public interface OnlineEvent {
    /**
     * Execute the event with an online profile.
     *
     * @param profile online profile to run the event with
     * @throws QuestRuntimeException if the execution of the event fails
     */
    void execute(OnlineProfile profile) throws QuestRuntimeException;
}
