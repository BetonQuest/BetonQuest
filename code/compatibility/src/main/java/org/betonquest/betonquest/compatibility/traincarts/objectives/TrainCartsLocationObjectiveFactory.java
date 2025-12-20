package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link TrainCartsLocationObjective} instances from {@link DefaultInstruction}s.
 */
public class TrainCartsLocationObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the TrainCartsLocationObjectiveFactory.
     */
    public TrainCartsLocationObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.getValue("range", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new TrainCartsLocationObjective(instruction, loc, range);
    }
}
