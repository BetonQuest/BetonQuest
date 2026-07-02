package org.betonquest.betonquest.api.function;

/**
 * Represents a value assignment to a function.
 *
 * @since 3.1.0
 */
public interface FunctionAssignment {

    /**
     * Returns the value as a string.
     *
     * @return the value as a string
     * @since 3.1.0
     */
    String asString();

    /**
     * Returns the value as a number.
     *
     * @return the value as a number
     * @since 3.1.0
     */
    Number asNumber();

    /**
     * Returns the value as a boolean.
     *
     * @return the value as a boolean
     * @since 3.1.0
     */
    boolean asBoolean();
}
