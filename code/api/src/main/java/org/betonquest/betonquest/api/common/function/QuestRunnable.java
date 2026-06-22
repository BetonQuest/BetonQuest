package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A simple {@link java.lang.Runnable} that can throw a {@link QuestException}.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface QuestRunnable {

    /**
     * Executes the runnable.
     *
     * @throws QuestException if the runnable fails
     * @since 3.0.0
     */
    void run() throws QuestException;
}
