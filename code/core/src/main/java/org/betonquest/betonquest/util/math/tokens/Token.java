package org.betonquest.betonquest.util.math.tokens;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Part of a parsed mathematical expression, that can be resolved to a number.
 *
 * @deprecated This should be replaced with a real expression parsing lib
 */
@Deprecated
@FunctionalInterface
public interface Token {

    /**
     * Resolves the tokens expression and returns the result.
     *
     * @param profile the {@link Profile} of the player for which this token should be resolved,
     *                required for parsing variables
     * @return the result
     * @throws QuestException if the Token contained variables that could not be resolved
     */
    double resolve(@Nullable Profile profile) throws QuestException;
}
