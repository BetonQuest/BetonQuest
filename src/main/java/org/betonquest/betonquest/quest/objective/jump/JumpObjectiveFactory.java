package org.betonquest.betonquest.quest.objective.jump;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory for creating {@link JumpObjective} instances from {@link Instruction}s.
 */
public class JumpObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the JumpObjectiveFactory.
     */
    public JumpObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new JumpObjective(instruction, targetAmount);
    }
}
