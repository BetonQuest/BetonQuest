package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

import java.time.DayOfWeek;
import java.util.Calendar;

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
    public boolean check() throws QuestException {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day == 1 ? 7 : day - 1;
        //As calendar.get(Calendar.DAY_OF_WEEK) returns 1 on sunday, 2 on monday, and so on this has to be fixed
        return day == this.day.getValue();
    }
}
