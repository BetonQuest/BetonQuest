package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Represent an argument that can be resolved to the given type.
 *
 * @param <T> the type of the argument
 */
@FunctionalInterface
public interface Argument<T> {

    /**
     * Gets the value of the argument for a given profile or no profile.
     *
     * @param profile the profile to resolve the arguments for or null if no profile is involved
     * @return the value of the argument
     * @throws QuestException if the argument could not be resolved
     */
    T getValue(@Nullable Profile profile) throws QuestException;
}
