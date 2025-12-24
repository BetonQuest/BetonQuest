package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;

/**
 * Checks if a given value is valid.
 *
 * @param <T> the type of the value to be checked
 */
@FunctionalInterface
public interface ValueValidator<T> {

    /**
     * Checks if the value is valid.
     * If this method returns false, a very simple exception will be thrown.
     * To specify details about the error, throw a {@link QuestException} with a custom message instead.
     *
     * @param value the value to check
     * @return true if the value is valid, false otherwise
     * @throws QuestException if the value is invalid
     */
    boolean validate(T value) throws QuestException;
}
