package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link MMOCoreBreakCustomBlockObjective} instances from {@link Instruction}s.
 */
public class MMOCoreBreakCustomBlockObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MMOCoreBreakCustomBlockObjectiveFactory.
     */
    public MMOCoreBreakCustomBlockObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> desiredBlockId = instruction.getValue("block", Argument.STRING);
        if (desiredBlockId == null) {
            throw new QuestException("Missing required argument: block");
        }
        final Variable<Number> targetAmount = instruction.get(Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new MMOCoreBreakCustomBlockObjective(instruction, targetAmount, desiredBlockId);
    }
}
