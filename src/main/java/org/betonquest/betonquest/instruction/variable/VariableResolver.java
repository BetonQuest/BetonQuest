package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;

/**
 * A variable resolver is a function that resolves a variable to a value.
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface VariableResolver<T> extends QuestFunction<String, T> {
    /**
     * Clones the value.
     * This is necessary for mutable values.
     *
     * @param value the value to clone
     * @return the cloned value
     */
    default T clone(final T value) {
        return value;
    }
}
