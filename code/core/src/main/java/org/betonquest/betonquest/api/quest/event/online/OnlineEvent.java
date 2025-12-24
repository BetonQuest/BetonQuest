package org.betonquest.betonquest.api.quest.event.online;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Quest event that needs an online profile to function correctly.
 */
@FunctionalInterface
public interface OnlineEvent extends PrimaryThreadEnforceable {

    /**
     * Execute the event with an online profile.
     *
     * @param profile online profile to run the event with
     * @throws QuestException if the execution of the event fails
     */
    void execute(OnlineProfile profile) throws QuestException;
}
