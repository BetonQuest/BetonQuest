package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents a fallback assignment that throws an {@link UnsupportedOperationException} if none is defined explicitly.
 *
 * @since 3.1.0
 */
public class DefaultFallbackAssignment implements FunctionAssignment {

    /**
     * Creates a new {@link DefaultFallbackAssignment}.
     *
     * @since 3.1.0
     */
    public DefaultFallbackAssignment() {
    }

    @Override
    public String asString() {
        throw new UnsupportedOperationException("Function variable is not defined.");
    }

    @Override
    public Number asNumber() {
        throw new UnsupportedOperationException("Function variable is not defined.");
    }

    @Override
    public boolean asBoolean() {
        throw new UnsupportedOperationException("Function variable is not defined.");
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
