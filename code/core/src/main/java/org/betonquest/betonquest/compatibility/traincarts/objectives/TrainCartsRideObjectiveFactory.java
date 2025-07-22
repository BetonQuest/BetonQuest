package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

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
        final Variable<String> name = instruction.getValue("name", Argument.STRING, "");
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new TrainCartsRideObjective(instruction, targetAmount, name);
    }
}
