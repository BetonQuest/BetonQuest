package org.betonquest.betonquest.api.quest.condition.online;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Quest condition that needs an online profile to function correctly.
 */
public interface OnlineCondition {
    /**
     * Checks the condition with an online profile.
     *
     * @param profile online profile to check the condition with
     * @return if the condition is fulfilled
     * @throws QuestException when the condition check fails
     */
    boolean check(OnlineProfile profile) throws QuestException;
}
