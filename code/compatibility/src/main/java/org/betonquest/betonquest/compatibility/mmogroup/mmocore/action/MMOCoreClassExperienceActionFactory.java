package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create {@link MMOCoreClassExperienceAction}s from {@link Instruction}s.
 */
public class MMOCoreClassExperienceActionFactory implements PlayerActionFactory {

    /**
     * Create a new MMO Core Action Factory.
     */
    public MMOCoreClassExperienceActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {

        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> level = instruction.bool().getFlag("level", true);
        return new MMOCoreClassExperienceAction(amount, level);
    }
}
