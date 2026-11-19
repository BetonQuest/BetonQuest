package org.betonquest.betonquest.particle.deltafunction;

import org.betonquest.betonquest.api.QuestException;

/**
 * Represents a two-dimensional delta function.
 */
@FunctionalInterface
public interface DeltaFunction {

    /**
     * Evaluates the function with a given delta value pair.
     *
     * @param firstDelta  the first delta to evaluate the function with
     * @param secondDelta the second delta to evaluate the function with
     * @return the result of the function
     * @throws QuestException if the function fails to evaluate
     */
    Number evaluate(Number firstDelta, Number secondDelta) throws QuestException;
}
