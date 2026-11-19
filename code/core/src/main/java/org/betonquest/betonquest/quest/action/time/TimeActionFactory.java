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
import org.betonquest.betonquest.lib.instruction.argument.DefaultArguments;
import org.betonquest.betonquest.quest.action.ThrowExceptionPlayerlessAction;
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
        if (!instruction.hasOptional("world")) {
            return new ThrowExceptionPlayerlessAction("Action requires a 'world' argument.");
        }
        return createTimeAction(instruction);
    }

    private NullableActionAdapter createTimeAction(final Instruction instruction) throws QuestException {
        final Argument<TimeChange> time = instruction.parse(TimeParser.TIME).get();
        final Argument<World> world = instruction.world().get("world").orElse(DefaultArguments.PLAYER_WORLD);
        final FlagArgument<Boolean> tickFormat = instruction.bool().getFlag("ticks", true);
        return new NullableActionAdapter(new TimeAction(time, world, tickFormat));
    }
}
