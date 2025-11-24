package org.betonquest.betonquest.quest.condition.time.real;

import java.util.Locale;

/**
 * Value validation component for use in {@link TimeInterval}.
 */
public enum PartialDate {
    /**
     * The valid range of days.
     * <p>
     * Maximum 31 days in a months.
     */
    DAY(31),
    /**
     * The valid range of months.
     * <p>
     * 12 months in a year.
     */
    MONTH(12),
    /**
     * The valid range of years.
     * <p>
     * They are just positive.
     */
    YEAR(-1);

    /**
     * Maximal value of the category.
     */
    private final int maxValue;

    PartialDate(final int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Checks if the value is valid for this type.
     *
     * @param max the value to check
     * @return if the value is in range
     */
    public boolean isInvalid(final int max) {
        return max <= 0 || (maxValue != -1 && max > maxValue);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
