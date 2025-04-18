package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final String desiredBlockId = instruction.getOptionalArgument("block")
                .orElseThrow(() -> new QuestException("Missing required argument: block"));
        final VariableNumber targetAmount = instruction.get(VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new MMOCoreBreakCustomBlockObjective(instruction, targetAmount, desiredBlockId);
    }
}
