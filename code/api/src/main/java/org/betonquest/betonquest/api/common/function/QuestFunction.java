package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A simple {@link java.util.function.Function} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @since 3.0.0
 */
@FunctionalInterface
public interface QuestFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param arg the function argument
     * @return the function result
     * @throws QuestException if the function execution fails
     * @since 3.0.0
     */
    R apply(T arg) throws QuestException;
}
