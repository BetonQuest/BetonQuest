package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.quest.condition.time.Time;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;

import java.time.LocalDateTime;

/**
 * A condition that checks the real time.
 */
public class RealTimeCondition implements PlayerlessCondition {

    /**
     * The time frame in which the time should be.
     */
    private final Variable<TimeFrame> timeFrame;

    /**
     * Creates a new RealTimeCondition.
     *
     * @param timeFrame the time frame
     */
    public RealTimeCondition(final Variable<TimeFrame> timeFrame) {
        this.timeFrame = timeFrame;
    }

    @Override
    public boolean check() throws QuestException {
        final LocalDateTime now = LocalDateTime.now();
        final Time currentTime = new Time(now.getHour(), now.getMinute());
        return timeFrame.getValue(null).isTimeBetween(currentTime);
    }
}
