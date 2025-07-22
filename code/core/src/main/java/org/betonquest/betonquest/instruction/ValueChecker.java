package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Checks if a given value is valid.
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface ValueChecker<T> {
    /**
     * Checks if the value of the variable is valid.
     *
     * @param value the value to check
     * @throws QuestException if the value is invalid
     */
    void check(T value) throws QuestException;
}
