package org.betonquest.betonquest.api.quest.variable.online;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Player Variable that needs an online profile to function correctly.
 */
@FunctionalInterface
public interface OnlineVariable {
    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link OnlineProfile} to get the value for
     * @return the value of this variable
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(OnlineProfile profile) throws QuestException;
}
