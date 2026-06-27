package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents an inverted function assignment.
 *
 * @since 3.1.0
 */
public class BooleanInvertedFunctionAssignment implements FunctionAssignment {

    /**
     * The assignment to invert.
     */
    private final FunctionAssignment assignment;

    /**
     * Creates a new inverted function assignment.
     *
     * @param assignment the assignment to invert
     * @since 3.1.0
     */
    public BooleanInvertedFunctionAssignment(final FunctionAssignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public String asString() {
        return String.valueOf(asBoolean());
    }

    @Override
    public Number asNumber() {
        return asBoolean() ? 1 : 0;
    }

    @Override
    public boolean asBoolean() {
        return !assignment.asBoolean();
    }

    @Override
    public String toString() {
        return asString();
    }
}
