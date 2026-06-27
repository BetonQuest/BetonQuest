package org.betonquest.betonquest.lib.function.assignment;

import org.betonquest.betonquest.api.function.FunctionAssignment;

/**
 * Represents a string source assignment.
 *
 * @since 3.1.0
 */
public class StringSourceAssignment implements FunctionAssignment {

    /**
     * The source string.
     */
    private final String source;

    /**
     * Creates a new StringSourceAssignment.
     *
     * @param source the source string
     * @since 3.1.0
     */
    public StringSourceAssignment(final String source) {
        this.source = source;
    }

    @Override
    public String asString() {
        return source;
    }

    @Override
    public Number asNumber() {
        return source.matches("^-?[0-9]+$") ? Long.parseLong(source)
                : source.matches("^-?[0-9]+(\\.[0-9]+)?$") ? Double.parseDouble(source) : Double.NaN;
    }

    @Override
    public boolean asBoolean() {
        return "true".equalsIgnoreCase(source) || asNumber().intValue() > 0;
    }

    @Override
    public String toString() {
        return asString();
    }
}
