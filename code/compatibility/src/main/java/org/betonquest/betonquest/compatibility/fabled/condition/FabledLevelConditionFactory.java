package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsLevelCondition;

/**
 * Factory to create {@link AuraSkillsLevelCondition}s from {@link DefaultInstruction}s.
 */
public class FabledLevelConditionFactory implements PlayerConditionFactory {

    /**
     * The data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     *
     * @param data the data used for primary server access.
     */
    public FabledLevelConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> classNameVar = instruction.get(Argument.STRING);
        final Variable<Number> levelVar = instruction.get(Argument.NUMBER);
        return new PrimaryServerThreadPlayerCondition(new FabledLevelCondition(classNameVar, levelVar), data);
    }
}
