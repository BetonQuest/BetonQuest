package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link TrainCartsExitObjective} instances from {@link Instruction}s.
 */
public class TrainCartsExitObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TrainCartsExitObjectiveFactory.
     */
    public TrainCartsExitObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<String> name = instruction.getValue("name", Argument.STRING, "");
        return new TrainCartsExitObjective(instruction, name);
    }
}
