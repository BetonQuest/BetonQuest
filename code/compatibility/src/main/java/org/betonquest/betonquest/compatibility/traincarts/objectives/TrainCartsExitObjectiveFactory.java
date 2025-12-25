package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

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
        final Argument<String> name = instruction.string().get("name", "");
        return new TrainCartsExitObjective(instruction, name);
    }
}
