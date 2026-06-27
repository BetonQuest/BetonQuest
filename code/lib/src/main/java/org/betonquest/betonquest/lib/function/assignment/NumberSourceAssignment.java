package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents a number source assignment.
 *
 * @since 3.1.0
 */
public class NumberSourceAssignment implements FunctionAssignment {

    /**
     * The source number.
     */
    private final Number source;

    /**
     * Creates a new NumberSourceAssignment.
     *
     * @param source the source number
     * @since 3.1.0
     */
    public NumberSourceAssignment(final Number source) {
        this.source = source;
    }

    @Override
    public String asString() {
        return String.valueOf(source);
    }

    @Override
    public Number asNumber() {
        return source;
    }

    @Override
    public boolean asBoolean() {
        return asNumber().longValue() > 0;
    }

    @Override
    public String toString() {
        return asString();
    }
}
