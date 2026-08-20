package org.betonquest.betonquest.item.typehandler;

/**
 * The existence of Item parts.
 */
public enum Existence {
    /**
     * Must be present.
     */
    REQUIRED,
    /**
     * Not allowed.
     */
    FORBIDDEN,
    /**
     * Not relevant.
     */
    WHATEVER;

    /**
     * Value forbidding the existence of a value.
     */
    public static final String NONE_KEY = "none";
}
