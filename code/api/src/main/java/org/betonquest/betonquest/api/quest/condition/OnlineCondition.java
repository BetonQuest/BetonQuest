package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Quest condition that needs an online profile to function correctly.
 */
@FunctionalInterface
public interface OnlineCondition extends PrimaryThreadEnforceable {

    /**
     * Checks the condition with an online profile.
     *
     * @param profile online profile to check the condition with
     * @return if the condition is fulfilled
     * @throws QuestException when the condition check fails
     */
    boolean check(OnlineProfile profile) throws QuestException;
}
