package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;

/**
 * Factory to create real time conditions from {@link DefaultInstruction}s.
 */
public class RealTimeConditionFactory implements PlayerlessConditionFactory {

    /**
     * Create the real time condition factory.
     */
    public RealTimeConditionFactory() {
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        final Variable<TimeFrame> timeFrame = instruction.get(TimeFrame::parse);
        return new RealTimeCondition(timeFrame);
    }
}
