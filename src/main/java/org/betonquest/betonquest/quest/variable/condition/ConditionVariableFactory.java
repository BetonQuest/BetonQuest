package org.betonquest.betonquest.quest.variable.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;

/**
 * Factory to create {@link ConditionVariable}s from {@link Instruction}s.
 */
public class ConditionVariableFactory implements PlayerVariableFactory {
    /**
     * Create the Condition Variable Factory.
     */
    public ConditionVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws InstructionParseException {
        final ConditionID conditionId = instruction.getCondition();
        final boolean papiMode = instruction.hasArgument("papiMode");
        return new ConditionVariable(conditionId, papiMode);
    }
}
