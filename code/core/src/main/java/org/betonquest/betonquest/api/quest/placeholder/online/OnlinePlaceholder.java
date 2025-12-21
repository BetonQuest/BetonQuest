package org.betonquest.betonquest.api.quest.placeholder.online;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Player Placeholder that needs an online profile to function correctly.
 */
@FunctionalInterface
public interface OnlinePlaceholder extends PrimaryThreadEnforceable {

    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link OnlineProfile} to get the value for
     * @return the value of this placeholder
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(OnlineProfile profile) throws QuestException;
}
