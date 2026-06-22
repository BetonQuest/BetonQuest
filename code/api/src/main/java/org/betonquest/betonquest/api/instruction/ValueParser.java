package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.common.function.QuestFunction;

/**
 * A {@link ValueParser} is essentially a function that resolves a string to a value.
 *
 * @param <R> the type of the resulting value
 * @since 3.0.0
 */
@FunctionalInterface
public interface ValueParser<R> extends QuestFunction<String, R> {

    /**
     * Clones the value, ensuring decoupling the object to prevent unwanted modifications.
     * This is necessary for mutable values.
     *
     * @param value the value to clone
     * @return the cloned value
     * @since 3.0.0
     */
    default R cloneValue(final R value) {
        return value;
    }
}
