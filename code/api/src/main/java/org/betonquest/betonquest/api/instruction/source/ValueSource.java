package org.betonquest.betonquest.api.instruction.source;

import org.jetbrains.annotations.Nullable;

/**
 * Describes the source of a value.
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface ValueSource<T> {

    /**
     * Get the value.
     *
     * @return the value
     */
    @Nullable
    T getValue();
}
