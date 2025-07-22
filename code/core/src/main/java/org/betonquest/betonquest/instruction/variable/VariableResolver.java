package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;

/**
 * A variable resolver is a function that resolves a variable to a value.
 *
 * @param <R> the type of the value
 */
@FunctionalInterface
public interface VariableResolver<R> extends QuestFunction<String, R> {
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
