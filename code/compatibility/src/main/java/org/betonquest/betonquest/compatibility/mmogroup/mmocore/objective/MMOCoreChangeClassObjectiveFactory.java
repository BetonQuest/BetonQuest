package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Argument<String> targetClassName = instruction.string().get("class").orElse(null);
        return new MMOCoreChangeClassObjective(instruction, targetClassName);
    }
}
