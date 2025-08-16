package org.betonquest.betonquest.quest.objective.jump;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new JumpObjective(instruction, targetAmount);
    }
}
