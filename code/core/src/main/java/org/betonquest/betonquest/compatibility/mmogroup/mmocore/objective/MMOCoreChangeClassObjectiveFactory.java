package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link MMOCoreChangeClassObjective} instances from {@link Instruction}s.
 */
public class MMOCoreChangeClassObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the MMOCoreChangeClassObjectiveFactory.
     */
    public MMOCoreChangeClassObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> targetClassName = instruction.getValue("class", Argument.STRING);
        return new MMOCoreChangeClassObjective(instruction, targetClassName);
    }
}
