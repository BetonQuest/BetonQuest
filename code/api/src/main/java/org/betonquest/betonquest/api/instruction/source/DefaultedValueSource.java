package org.betonquest.betonquest.api.instruction.source;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Describes a value source which is defaulted to another.
 *
 * @param <T> the type of the value
 * @since 3.0.0
 */
public interface DefaultedValueSource<T> extends ValueSource<T> {

    /**
     * Gets the default fallback source.
     *
     * @return the default fallback source
     * @since 3.0.0
     */
    ValueSource<T> getDefault();

    /**
     * Gets the value or the default value if the value is invalid.
     *
     * @param predicate the predicate to check if the value is valid
     * @return the value or the default value
     * @since 3.0.0
     */
    @Nullable
    T getOrDefault(Predicate<T> predicate);
}
