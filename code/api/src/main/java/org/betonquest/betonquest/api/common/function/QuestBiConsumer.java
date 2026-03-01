package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

import java.util.function.BiConsumer;

/**
 * A simple {@link BiConsumer} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the first input to the operation
 * @param <U> the type of the second input to the operation
 */
@FunctionalInterface
public interface QuestBiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param firstArg  the first input argument
     * @param secondArg the second input argument
     * @throws QuestException when the method execution fails
     */
    void accept(T firstArg, U secondArg) throws QuestException;
}
