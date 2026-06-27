package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents an inverted function assignment.
 *
 * @since 3.1.0
 */
public class NumericInvertedFunctionAssignment implements FunctionAssignment {

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
    public NumericInvertedFunctionAssignment(final FunctionAssignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public String asString() {
        return String.valueOf(asNumber());
    }

    @Override
    public Number asNumber() {
        final Number number = assignment.asNumber();
        if (number instanceof Long) {
            return -number.longValue();
        }
        if (number instanceof Integer) {
            return -number.intValue();
        }
        return -number.doubleValue();
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
