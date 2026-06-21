package org.betonquest.betonquest.api.instruction;

import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * Represents an {@link Argument} that also is a flag.
 *
 * @param <T> the type of the argument
 * @see FlagState
 * @since 3.0.0
 */
@FunctionalInterface
public interface FlagArgument<T> extends Argument<Optional<T>> {

    /**
     * Retrieves the {@link FlagState} of the flag argument.
     *
     * @return the flag state
     * @since 3.0.0
     */
    @Contract(pure = true)
    default FlagState getState() {
        return FlagState.ABSENT;
    }
}
