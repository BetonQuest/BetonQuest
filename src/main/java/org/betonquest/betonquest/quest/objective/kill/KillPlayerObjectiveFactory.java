package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        final String name = instruction.getOptional("name");
        final List<ConditionID> required = instruction.getIDList(instruction.getOptional("required"), ConditionID::new);
        return new KillPlayerObjective(instruction, targetAmount, name, required);
    }
}
