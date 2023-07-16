package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A selector to get the appropriate target for a given profile.
 *
 * @param <T> target to select
 */
public interface Selector<T> {
    /**
     * Get the target that should be used for the given profile.
     *
     * @param profile profile to get the target for
     * @return the appropriate target to use
     * @throws QuestRuntimeException if the target to use cannot be determined
     */
    T selectFor(Profile profile) throws QuestRuntimeException;
}
