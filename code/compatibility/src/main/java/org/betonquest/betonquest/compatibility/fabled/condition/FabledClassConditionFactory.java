package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
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
        final Argument<String> classNameVar = instruction.string().get();
        final boolean exact = instruction.hasArgument("exact");
        return new FabledClassCondition(classNameVar, exact);
    }
}
