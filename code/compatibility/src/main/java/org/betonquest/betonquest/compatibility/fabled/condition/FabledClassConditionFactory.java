package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link FabledClassCondition}s from {@link Instruction}s.
 */
public class FabledClassConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     */
    public FabledClassConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> className = instruction.string().get();
        final FlagArgument<Boolean> exact = instruction.bool().getFlag("exact", true);
        return new FabledClassCondition(className, exact);
    }
}
