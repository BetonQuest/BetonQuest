package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link MMOCoreAttributeReallocationPointsAction}s from {@link Instruction}s.
 */
public class MMOCoreAttributeReallocationPointsActionFactory implements PlayerActionFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreAttributeReallocationPointsActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        return new MMOCoreAttributeReallocationPointsAction(amount);
    }
}
