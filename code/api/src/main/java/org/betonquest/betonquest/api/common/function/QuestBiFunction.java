package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A simple {@link java.util.function.BiFunction} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the first input to the function
 * @param <U> the type of the second input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface QuestBiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param arg  the first function argument
     * @param arg2 the second function argument
     * @return the function result
     * @throws QuestException if the function execution fails
     */
    R apply(T arg, U arg2) throws QuestException;
}
