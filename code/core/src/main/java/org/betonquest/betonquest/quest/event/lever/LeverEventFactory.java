package org.betonquest.betonquest.quest.event.lever;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

/**
 * Factory for {@link LeverEvent}.
 */
public class LeverEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create a new LeverEventFactory.
     */
    public LeverEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createLeverEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createLeverEvent(instruction);
    }

    private NullableEventAdapter createLeverEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Argument<StateType> stateType = instruction.enumeration(StateType.class).get();
        return new NullableEventAdapter(new LeverEvent(stateType, location));
    }
}
