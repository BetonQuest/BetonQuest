package org.betonquest.betonquest.api.instruction.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;

/**
 * A {@link ValueParser} is essentially a function that resolves a string to a value.
 *
 * @param <R> the type of the resulting value
 */
@FunctionalInterface
public interface ValueParser<R> extends QuestFunction<String, R> {

    /**
     * Clones the value, ensuring decoupling the object to prevent unwanted modifications.
     * This is necessary for mutable values.
     *
     * @param value the value to clone
     * @return the cloned value
     */
    default R cloneValue(final R value) {
        return value;
    }
}
