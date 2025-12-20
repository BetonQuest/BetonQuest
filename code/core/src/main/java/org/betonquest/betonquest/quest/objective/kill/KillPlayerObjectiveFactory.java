package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link KillPlayerObjective} instances from {@link DefaultInstruction}s.
 */
public class KillPlayerObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the KillPlayerObjectiveFactory.
     */
    public KillPlayerObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> name = instruction.getValue("name", Argument.STRING);
        final Variable<List<ConditionID>> required = instruction.getValueList("required", ConditionID::new);
        return new KillPlayerObjective(instruction, targetAmount, name, required);
    }
}
