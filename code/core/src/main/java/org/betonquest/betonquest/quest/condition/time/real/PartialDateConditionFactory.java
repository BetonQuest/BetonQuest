package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;

import java.util.List;

/**
 * Factory to create partial date conditions from {@link Instruction}s.
 */
public class PartialDateConditionFactory implements PlayerlessConditionFactory {

    /**
     * Create a new PartialDate condition factory.
     */
    public PartialDateConditionFactory() {
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String dayOfMonthString = instruction.getValue("day");
        final List<TimeInterval> dayOfMonth;
        if (dayOfMonthString == null) {
            dayOfMonth = null;
        } else {
            dayOfMonth = TimeInterval.parseFromString(dayOfMonthString, PartialDate.DAY);
        }
        final String monthString = instruction.getValue("month");
        final List<TimeInterval> month;
        if (monthString == null) {
            month = null;
        } else {
            month = TimeInterval.parseFromString(monthString, PartialDate.MONTH);
        }
        final String yearString = instruction.getValue("year");
        final List<TimeInterval> year;
        if (yearString == null) {
            year = null;
        } else {
            year = TimeInterval.parseFromString(yearString, PartialDate.YEAR);
        }
        return new PartialDateCondition(dayOfMonth, month, year);
    }
}
