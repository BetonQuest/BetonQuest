package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents a boolean source assignment.
 *
 * @since 3.1.0
 */
public class BooleanSourceAssignment implements FunctionAssignment {

    /**
     * The source boolean.
     */
    private final boolean source;

    /**
     * Creates a new BooleanSourceAssignment.
     *
     * @param source the source boolean
     * @since 3.1.0
     */
    public BooleanSourceAssignment(final boolean source) {
        this.source = source;
    }

    @Override
    public String asString() {
        return Boolean.toString(source);
    }

    @Override
    public Number asNumber() {
        return source ? 1 : 0;
    }

    @Override
    public boolean asBoolean() {
        return source;
    }

    @Override
    public String toString() {
        return asString();
    }
}
