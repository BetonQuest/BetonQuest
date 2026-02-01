package org.betonquest.betonquest.quest.action.time;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.quest.action.DoNothingPlayerlessAction;
import org.bukkit.World;

/**
 * Factory to create time actions from {@link Instruction}s.
 */
public class TimeActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Creates the time action factory.
     *
     */
    public TimeActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createTimeAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        if (instruction.copy().string().get("world").isEmpty()) {
            return new DoNothingPlayerlessAction();
        }
        return createTimeAction(instruction);
    }

    private NullableActionAdapter createTimeAction(final Instruction instruction) throws QuestException {
        final Argument<TimeChange> time = instruction.parse(TimeParser.TIME).get();
        final String worldPart = instruction.string().get("world", "%location.world%").getValue(null);
        final Argument<World> world = instruction.chainForArgument(worldPart).world().get();
        final FlagArgument<Boolean> tickFormat = instruction.bool().getFlag("ticks", true);
        return new NullableActionAdapter(new TimeAction(time, world, tickFormat));
    }
}
