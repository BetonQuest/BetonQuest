package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
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
        final LocalDate localDate = LocalDate.now();
        if (dayOfMonth != null
                && this.dayOfMonth.stream().noneMatch(interval -> interval.isWithin(localDate.getDayOfMonth()))) {
            return false;
        }
        if (month != null
                && this.month.stream().noneMatch(interval -> interval.isWithin(localDate.getMonthValue()))) {
            return false;
        }
        return year == null
                || this.year.stream().anyMatch(interval -> interval.isWithin(localDate.getYear()));
    }
}
