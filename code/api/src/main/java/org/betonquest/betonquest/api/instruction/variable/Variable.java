package org.betonquest.betonquest.api.instruction.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a variable that can be resolved to the given type.
 *
 * @param <T> the type of the variable
 */
@FunctionalInterface
public interface Variable<T> {

    /**
     * Gets the value of the variable for a given profile or no profile.
     *
     * @param profile the profile to resolve the variables for or null if no profile is involved
     * @return the value of the variable
     * @throws QuestException if the variable could not be resolved
     */
    T getValue(@Nullable Profile profile) throws QuestException;
}
