package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

/**
 * Factory for creating {@link TrainCartsLocationObjective} instances from {@link Instruction}s.
 */
public class TrainCartsLocationObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the TrainCartsLocationObjectiveFactory.
     */
    public TrainCartsLocationObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.get(VariableLocation::new);
        final VariableNumber range = instruction.get(instruction.getOptional("range", "1"), VariableArgument.NUMBER_NOT_LESS_THAN_ONE);
        return new TrainCartsLocationObjective(instruction, loc, range);
    }
}
