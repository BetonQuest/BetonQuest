package org.betonquest.betonquest.quest.objective.variable;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final boolean noChat = instruction.bool().getFlag("no-chat", false)
                .getValue(null).orElse(false);
        if (noChat) {
            return new VariableObjective(instruction);
        }
        return new ChatVariableObjective(instruction);
    }
}
