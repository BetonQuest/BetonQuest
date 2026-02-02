package org.betonquest.betonquest.quest.condition.time.ingame;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;
import org.bukkit.World;

import java.util.Optional;

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
        final Argument<TimeFrame> timeFrame = instruction.parse(TimeFrame::parse).get();
        final String worldRaw = instruction.string().get("world", "%location.world%").getValue(null);
        final Argument<World> world = instruction.chainForArgument(worldRaw).world().get();
        return new NullableConditionAdapter(new TimeCondition(timeFrame, world));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Optional<Argument<World>> world = instruction.world().get("world");
        if (world.isEmpty()) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Argument<TimeFrame> timeFrame = instruction.parse(TimeFrame::parse).get();
        return new NullableConditionAdapter(new TimeCondition(timeFrame, world.get()));
    }
}
