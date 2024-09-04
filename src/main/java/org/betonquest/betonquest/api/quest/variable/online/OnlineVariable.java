package org.betonquest.betonquest.api.quest.variable.online;

import org.betonquest.betonquest.api.profiles.OnlineProfile;

/**
 * Player Variable that needs an online profile to function correctly.
 */
public interface OnlineVariable {
    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link OnlineProfile} to get the value for
     * @return the value of this variable
     */
    String getValue(OnlineProfile profile);
}
