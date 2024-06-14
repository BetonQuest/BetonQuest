package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Interface for quest-conditions that are checked for an online profile.
 * The difference to the {@link PlayerCondition} is the guaranteed availability of a player instance.
 *
 * @see PlayerCondition
 */
public interface OnlinePlayerCondition {
    /**
     * Checks the condition.
     *
     * @param profile the {@link OnlineProfile} the condition is checked for
     * @return if the condition is fulfilled
     * @throws QuestRuntimeException when the condition check fails
     */
    boolean check(OnlineProfile profile) throws QuestRuntimeException;
}
