package org.betonquest.betonquest.quest.condition.selector;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;

import javax.annotation.Nullable;

/**
 * Selector interface to check if a value matches other selected values.
 */
public interface Selector {

    /**
     * Checks if the given value matches the given selected values.
     *
     * @param profile the profile to check against
     * @param value   the value to check
     * @return whether the value matches the selected values
     * @throws QuestException if an error occurs while checking the condition
     */
    boolean matches(@Nullable Profile profile, Number value) throws QuestException;
}
