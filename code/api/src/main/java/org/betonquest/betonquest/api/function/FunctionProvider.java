package org.betonquest.betonquest.api.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * Provides {@link MathFunction}s by their identifier as raw strings.
 *
 * @since 3.1.0
 */
public interface FunctionProvider {

    /**
     * Retrieves a {@link MathFunction} by its identifier.
     *
     * @param identifier the identifier of the function
     * @return the math function
     * @throws QuestException if a function with that identifier does not exist
     * @since 3.1.0
     */
    MathFunction getFunction(String identifier) throws QuestException;

    /**
     * Retrieves a subroutine by its name.
     *
     * @param name the name of the subroutine
     * @return the subroutine
     * @throws QuestException if a subroutine with that name does not exist
     */
    MathFunction getSubRoutine(String name) throws QuestException;
}
