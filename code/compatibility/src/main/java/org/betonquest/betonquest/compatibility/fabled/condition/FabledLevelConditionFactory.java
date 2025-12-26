package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsLevelCondition;

/**
 * Factory to create {@link AuraSkillsLevelCondition}s from {@link Instruction}s.
 */
public class FabledLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     */
    public FabledLevelConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> className = instruction.string().get();
        final Argument<Number> level = instruction.number().get();
        return new FabledLevelCondition(className, level);
    }
}
