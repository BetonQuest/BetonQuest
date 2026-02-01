package org.betonquest.betonquest.api.logger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.common.function.QuestSupplier;

/**
 * Can handle thrown {@link QuestException} and rate limits them.
 * Useful in cases where errors might be thrown periodically.
 */
public interface QuestExceptionHandler {

    /**
     * Runs a task and logs occurring quest exceptions with a rate limit.
     *
     * @param qeThrowing   a task that may throw a quest exception
     * @param defaultValue the default value to return in case of an exception
     * @param <T>          the type of the result
     * @return the result of the task or the default value if an exception occurs
     */
    <T> T handle(QuestSupplier<T> qeThrowing, T defaultValue);

    /**
     * Runs a task and logs occurring quest exceptions with a rate limit.
     *
     * @param qeThrowing a task that may throw a quest exception
     */
    void handle(QuestRunnable qeThrowing);
}
