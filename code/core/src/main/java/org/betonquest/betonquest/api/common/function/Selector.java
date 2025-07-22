package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * A selector to get the appropriate target for a given profile.
 *
 * @param <T> target to select
 */
@FunctionalInterface
public interface Selector<T> {
    /**
     * Gets the target that should be used for the given profile.
     *
     * @param profile profile to get the target for
     * @return the appropriate target to use
     * @throws QuestException if the target to use cannot be determined
     */
    T selectFor(@Nullable Profile profile) throws QuestException;
}
