package org.betonquest.betonquest.compatibility.mmogroup.mmocore.objective;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> professionName = instruction.string().get();
        final Argument<Number> targetLevel = instruction.number().get();
        return new MMOCoreProfessionObjective(instruction, professionName, targetLevel);
    }
}
