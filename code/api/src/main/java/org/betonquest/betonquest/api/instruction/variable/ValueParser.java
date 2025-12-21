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
     * Clones the value.
     * This is necessary for mutable values.
     *
     * @param value the value to clone
     * @return the cloned value
     */
    default R clone(final R value) {
        return value;
    }
}
