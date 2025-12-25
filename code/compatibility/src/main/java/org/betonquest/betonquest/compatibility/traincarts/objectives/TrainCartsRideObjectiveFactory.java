package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

/**
 * Factory for creating {@link TrainCartsRideObjective} instances from {@link Instruction}s.
 */
public class TrainCartsRideObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TrainCartsRideObjectiveFactory.
     */
    public TrainCartsRideObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get("name", "");
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        return new TrainCartsRideObjective(instruction, targetAmount, name);
    }
}
