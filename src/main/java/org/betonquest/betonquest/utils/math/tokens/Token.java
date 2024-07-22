package org.betonquest.betonquest.utils.math.tokens;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Part of a parsed mathematical expression, that can be resolved to a number.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public interface Token {

    /**
     * Resolves the tokens expression and returns the result.
     *
     * @param profile the {@link Profile} of the player for which this token should be resolved,
     *                required for parsing variables
     * @return the result
     * @throws QuestRuntimeException if the Token contained variables that could not be resolved
     *                               due to an Quest Runtime exception
     */
    double resolve(@Nullable Profile profile) throws QuestRuntimeException;
}
