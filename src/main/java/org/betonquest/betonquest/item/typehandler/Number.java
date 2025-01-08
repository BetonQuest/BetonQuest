package org.betonquest.betonquest.item.typehandler;

/**
 * Defines how a number value is compared to a stored value.
 */
public enum Number {
    /**
     * Needs to be the same.
     */
    EQUAL,
    /**
     * Actual needs to be higher than reference.
     */
    MORE,
    /**
     * Actual needs to be below reference.
     */
    LESS,
    /**
     * Not relevant.
     */
    WHATEVER
}
