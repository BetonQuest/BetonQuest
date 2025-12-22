package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
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
        final Variable<Location> loc = instruction.get(instruction.getParsers().location());
        final Variable<Number> range = instruction.getValue("range", instruction.getParsers().number().atLeast(1), 1);
        return new TrainCartsLocationObjective(instruction, loc, range);
    }
}
