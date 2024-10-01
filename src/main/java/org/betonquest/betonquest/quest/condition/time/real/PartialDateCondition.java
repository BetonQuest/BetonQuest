package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;

/**
 * Checks if the current date matches one of the given dates.
 */
public class PartialDateCondition implements PlayerlessCondition {
    /**
     * Days to match or null if not relevant.
     */
    @Nullable
    private final List<TimeInterval> dayOfMonth;

    /**
     * Months to match or null if not relevant.
     */
    @Nullable
    private final List<TimeInterval> month;

    /**
     * Years to match or null if not relevant.
     */
    @Nullable
    private final List<TimeInterval> year;

    /**
     * Create a new PartialDate condition.
     *
     * @param dayOfMonth the day to match or null if not specified
     * @param month      the month to match or null if not specified
     * @param year       the year to match or null if not specified
     */
    public PartialDateCondition(@Nullable final List<TimeInterval> dayOfMonth, @Nullable final List<TimeInterval> month,
                                @Nullable final List<TimeInterval> year) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean check() {
        final Calendar current = Calendar.getInstance();
        if (dayOfMonth != null) {
            final int day = current.get(Calendar.DAY_OF_MONTH);
            if (this.dayOfMonth.stream().noneMatch(interval -> interval.isWithin(day))) {
                return false;
            }
            //if day is not one of the given ones return false
        }
        if (month != null) {
            final int month = current.get(Calendar.MONTH) + 1;
            //Don't ask why +1: java.util.Calendar is a complete mess (january is 0, december is 11,...)
            if (this.month.stream().noneMatch(interval -> interval.isWithin(month))) {
                return false;
            }
            //if month is not one of the given ones return false
        }
        if (year != null) {
            final int year = current.get(Calendar.YEAR);
            return this.year.stream().anyMatch(interval -> interval.isWithin(year));
            //if year is not one of the given ones return false
        }
        return true;
    }
}
