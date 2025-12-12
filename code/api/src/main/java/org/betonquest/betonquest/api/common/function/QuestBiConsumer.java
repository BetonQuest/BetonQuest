package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A simple {@link java.util.function.BiConsumer} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the first input to the operation
 * @param <U> the type of the second input to the operation
 */
@FunctionalInterface
public interface QuestBiConsumer<T, U> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param arg1 the first input argument
     * @param arg2 the second input argument
     * @throws QuestException when the method execution fails
     */
    void accept(T arg1, U arg2) throws QuestException;
}
