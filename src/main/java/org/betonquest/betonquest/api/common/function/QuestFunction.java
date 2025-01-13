package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.exceptions.QuestException;

/**
 * A simple {@link java.util.function.Function} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface QuestFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param arg the function argument
     * @return the function result
     * @throws QuestException if the resolving fails
     */
    R apply(T arg) throws QuestException;
}
