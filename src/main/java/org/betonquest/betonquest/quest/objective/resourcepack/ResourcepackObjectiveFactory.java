package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;

/**
 * Factory for creating {@link ResourcepackObjective} instances from {@link Instruction}s.
 */
public class ResourcepackObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the ResourcepackObjectiveFactory.
     */
    public ResourcepackObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableString targetStatus = instruction.get(VariableString::new);
        return new ResourcepackObjective(instruction, targetStatus);
    }
}
