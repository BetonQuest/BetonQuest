package org.betonquest.betonquest.api.instruction;

import java.util.Optional;

/**
 * Represents an {@link Argument} that also is a flag.
 *
 * @param <T> the type of the argument
 * @see FlagState
 */
@FunctionalInterface
public interface FlagArgument<T> extends Argument<Optional<T>> {

    /**
     * Retrieves the {@link FlagState} of the flag argument.
     *
     * @return the flag state
     */
    default FlagState getState() {
        return FlagState.ABSENT;
    }
}
