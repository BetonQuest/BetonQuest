package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
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
        final Argument<List<TimeInterval>> day = instruction.string()
                .map(val -> TimeInterval.parseFromString(val, PartialDate.DAY))
                .get("day").orElse(null);
        final List<TimeInterval> dayOfMonth = day == null ? null : day.getValue(null);

        final Argument<List<TimeInterval>> month = instruction.string()
                .map(val -> TimeInterval.parseFromString(val, PartialDate.MONTH))
                .get("month").orElse(null);
        final List<TimeInterval> monthOfYear = month == null ? null : month.getValue(null);

        final Argument<List<TimeInterval>> year = instruction.string()
                .map(val -> TimeInterval.parseFromString(val, PartialDate.YEAR))
                .get("year").orElse(null);
        final List<TimeInterval> targetYear = year == null ? null : year.getValue(null);
        return new PartialDateCondition(dayOfMonth, monthOfYear, targetYear);
    }
}
