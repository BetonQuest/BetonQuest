package org.betonquest.betonquest.quest.action.lever;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.Location;

/**
 * Factory for {@link LeverAction}.
 */
public class LeverActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create a new LeverActionFactory.
     */
    public LeverActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createLeverAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createLeverAction(instruction);
    }

    private NullableActionAdapter createLeverAction(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final Argument<StateType> stateType = instruction.enumeration(StateType.class).get();
        return new NullableActionAdapter(new LeverAction(stateType, location));
    }
}
