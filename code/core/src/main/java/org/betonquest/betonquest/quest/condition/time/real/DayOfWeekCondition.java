package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * A condition that is true at a specific day in the week.
 */
public class DayOfWeekCondition implements PlayerlessCondition {

    /**
     * Day of the week this condition is true.
     */
    private final DayOfWeek day;

    /**
     * Create a new DayOfWeekCondition.
     *
     * @param dayOfWeek the day of the week this condition is true
     */
    public DayOfWeekCondition(final DayOfWeek dayOfWeek) {
        this.day = dayOfWeek;
    }

    @Override
    public boolean check() {
        return this.day == LocalDate.now().getDayOfWeek();
    }
}
