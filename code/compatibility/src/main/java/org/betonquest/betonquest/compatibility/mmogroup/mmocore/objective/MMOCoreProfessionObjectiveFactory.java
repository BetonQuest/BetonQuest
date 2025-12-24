package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link MMOCoreProfessionObjective} instances from {@link Instruction}s.
 */
public class MMOCoreProfessionObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOCoreProfessionObjectiveFactory.
     */
    public MMOCoreProfessionObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> professionName = instruction.string().get();
        final Variable<Number> targetLevel = instruction.number().get();
        return new MMOCoreProfessionObjective(instruction, professionName, targetLevel);
    }
}
