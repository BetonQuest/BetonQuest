package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
        final Variable<String> classNameVar = instruction.get(instruction.getParsers().string());
        final Variable<Number> levelVar = instruction.get(instruction.getParsers().number());
        return new FabledLevelCondition(classNameVar, levelVar);
    }
}
