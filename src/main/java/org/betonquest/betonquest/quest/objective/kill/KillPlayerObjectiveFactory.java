package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.List;

/**
 * Factory for creating {@link KillPlayerObjective} instances from {@link Instruction}s.
 */
public class KillPlayerObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the KillPlayerObjectiveFactory.
     */
    public KillPlayerObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        final Variable<String> name = instruction.getValue("name", Argument.STRING);
        final Variable<List<ConditionID>> required = instruction.getValueList("required", ConditionID::new);
        return new KillPlayerObjective(instruction, targetAmount, name, required);
    }
}
