package org.betonquest.betonquest.quest.condition.time.ingame;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;
import org.bukkit.World;

/**
 * Factory to create test for time conditions from {@link Instruction}s.
 */
public class TimeConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the test for time condition factory.
     */
    public TimeConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<TimeFrame> timeFrame = instruction.get(TimeFrame::parse);
        final Variable<World> world = instruction.get(instruction.getValue("world", "%location.world%"),
                instruction.getParsers().world());
        return new NullableConditionAdapter(new TimeCondition(timeFrame, world));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final String worldString = instruction.getValue("world");
        if (worldString == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<TimeFrame> timeFrame = instruction.get(TimeFrame::parse);
        final Variable<World> world = instruction.get(worldString, instruction.getParsers().world());
        return new NullableConditionAdapter(new TimeCondition(timeFrame, world));
    }
}
