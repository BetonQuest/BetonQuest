package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.exceptions.QuestException;

/**
 * A {@link java.util.concurrent.Callable} that may throw a {@link QuestException}.
 *
 * @param <R> the result type of the method call
 */
@FunctionalInterface
public interface QuestCallable<R> {

    /**
     * Calls the method and gets the result.
     *
     * @return result of the check
     * @throws QuestException when the method execution fails
     */
    R call() throws QuestException;
}
