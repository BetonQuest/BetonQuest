package org.betonquest.betonquest.quest.event.lever;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

/**
 * Factory for {@link LeverEvent}.
 */
public class LeverEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create a new LeverEventFactory.
     */
    public LeverEventFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createLeverEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createLeverEvent(instruction);
    }

    private NullableActionAdapter createLeverEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Argument<StateType> stateType = instruction.enumeration(StateType.class).get();
        return new NullableActionAdapter(new LeverEvent(stateType, location));
    }
}
