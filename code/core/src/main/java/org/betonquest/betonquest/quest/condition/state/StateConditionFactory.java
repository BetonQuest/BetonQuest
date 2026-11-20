package org.betonquest.betonquest.quest.condition.state;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for creating {@link StateCondition} instances from instruction data.
 */
public class StateConditionFactory implements PlayerConditionFactory {

    /**
     * Creates a new state condition factory.
     */
    public StateConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PlayerStates> playerState = instruction.enumeration(PlayerStates.class).get();
        return new OnlineConditionAdapter(new StateCondition(playerState));
    }
}
