package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

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
        final String profession = instruction.next();
        final String professionName = "MAIN".equalsIgnoreCase(profession) ? null : profession;
        final VariableNumber targetLevel = instruction.get(VariableNumber::new);
        return new MMOCoreProfessionObjective(instruction, professionName, targetLevel);
    }
}
