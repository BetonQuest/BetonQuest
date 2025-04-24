package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;

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
        final Variable<Location> loc = instruction.getVariable(Argument.LOCATION);
        final Variable<Number> range = instruction.getVariable(instruction.getOptional("range", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new TrainCartsLocationObjective(instruction, loc, range);
    }
}
