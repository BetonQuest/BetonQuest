package pl.betoncraft.betonquest.utils.math.tokens;

import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Part of a parsed mathematical expression, that can be resolved to a number.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * https://github.com/fasseg/exp4j
 */
@Deprecated
public interface Token {

    /**
     * Resolves the tokens expression and returns the result.
     *
     * @param playerID player for which this token should be resolved,
     *                 required for parsing variables
     * @return the result
     * @throws QuestRuntimeException if the Token contained variables that could not be resolved
     *                               due to an Quest Runtime exception
     */
    double resolve(final String playerID) throws QuestRuntimeException;
}
