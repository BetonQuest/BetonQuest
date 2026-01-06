package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link MMOCoreSkillPointsAction}s from {@link Instruction}s.
 */
public class MMOCoreAttributePointsActionFactory implements PlayerActionFactory {

    /**
     * Create a new MMO Core Event Factory.
     */
    public MMOCoreAttributePointsActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        return new MMOCoreAttributePointsAction(amount);
    }
}
