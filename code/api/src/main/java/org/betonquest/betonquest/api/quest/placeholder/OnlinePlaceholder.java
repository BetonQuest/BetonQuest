package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.jetbrains.annotations.Contract;

/**
 * Player Placeholder that needs an online profile to function correctly.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface OnlinePlaceholder extends PrimaryThreadEnforceable {

    /**
     * Gets the resolved value for the given profile.
     *
     * @param profile the {@link OnlineProfile} to get the value for
     * @return the value of this placeholder
     * @throws QuestException when the value could not be retrieved
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null -> new")
    String getValue(OnlineProfile profile) throws QuestException;
}
