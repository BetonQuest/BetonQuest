package org.betonquest.betonquest.api.quest.action.online;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Quest action that needs an online profile to function correctly.
 */
@FunctionalInterface
public interface OnlineAction extends PrimaryThreadEnforceable {

    /**
     * Execute the action with an online profile.
     *
     * @param profile online profile to run the action with
     * @throws QuestException if the execution of the action fails
     */
    void execute(OnlineProfile profile) throws QuestException;
}
