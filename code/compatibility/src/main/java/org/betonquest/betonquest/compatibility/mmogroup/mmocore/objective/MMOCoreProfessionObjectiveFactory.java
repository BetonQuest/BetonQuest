package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link MMOCoreProfessionObjective} instances from {@link DefaultInstruction}s.
 */
public class MMOCoreProfessionObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MMOCoreProfessionObjectiveFactory.
     */
    public MMOCoreProfessionObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> professionName = instruction.get(Argument.STRING);
        final Variable<Number> targetLevel = instruction.get(Argument.NUMBER);
        return new MMOCoreProfessionObjective(instruction, professionName, targetLevel);
    }
}
