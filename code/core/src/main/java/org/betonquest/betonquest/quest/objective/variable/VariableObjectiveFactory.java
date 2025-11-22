package org.betonquest.betonquest.quest.objective.variable;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link VariableObjective} instances from {@link Instruction}s.
 */
public class VariableObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new VariableObjectiveFactory instance.
     */
    public VariableObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        if (instruction.hasArgument("no-chat")) {
            return new VariableObjective(instruction);
        }
        return new ChatVariableObjective(instruction);
    }
}
