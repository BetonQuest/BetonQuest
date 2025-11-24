package org.betonquest.betonquest.item.typehandler;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Defines how a number value is compared to a stored value.
 */
public enum Number {
    /**
     * Needs to be the same.
     */
    EQUAL(Objects::equals),
    /**
     * Actual needs to be higher than reference.
     */
    MORE((value, base) -> value >= base),
    /**
     * Actual needs to be below reference.
     */
    LESS((value, base) -> value <= base),
    /**
     * Not relevant.
     */
    WHATEVER((value, base) -> true);

    /**
     * The number requirement.
     */
    private final BiPredicate<Integer, Integer> compare;

    Number(final BiPredicate<Integer, Integer> compare) {
        this.compare = compare;
    }

    /**
     * Test if the value matches the requirement.
     *
     * @param value the value to check
     * @param base  the stored base value to compare against
     * @return if the value matches
     */
    public boolean isValid(final int value, final int base) {
        return compare.test(value, base);
    }
}
