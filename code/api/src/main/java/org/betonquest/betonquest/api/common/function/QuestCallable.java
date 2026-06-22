package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A {@link java.util.concurrent.Callable} that may throw a {@link QuestException}.
 *
 * @param <R> the result type of the method call
 * @since 3.0.0
 */
@FunctionalInterface
public interface QuestCallable<R> {

    /**
     * Calls the method and gets the result.
     *
     * @return result of the check
     * @throws QuestException when the method execution fails
     * @since 3.0.0
     */
    R call() throws QuestException;
}
