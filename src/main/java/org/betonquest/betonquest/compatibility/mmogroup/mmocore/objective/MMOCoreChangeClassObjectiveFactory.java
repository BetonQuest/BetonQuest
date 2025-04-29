package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;

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
        final String targetClassName = instruction.getValue("class");
        return new MMOCoreChangeClassObjective(instruction, targetClassName);
    }
}
