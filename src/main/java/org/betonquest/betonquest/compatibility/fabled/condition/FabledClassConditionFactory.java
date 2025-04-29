package org.betonquest.betonquest.compatibility.fabled.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link FabledClassCondition}s from {@link Instruction}s.
 */
public class FabledClassConditionFactory implements PlayerConditionFactory {
    /**
     * The data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create AuraSkills Stats Conditions.
     *
     * @param data the data used for primary server access.
     */
    public FabledClassConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> classNameVar = instruction.get(Argument.STRING);
        final boolean exact = instruction.hasArgument("exact");
        return new PrimaryServerThreadPlayerCondition(new FabledClassCondition(classNameVar, exact), data);
    }
}
