package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().atLeast(1).get("range", 1);
        final TrainCartsLocationObjective objective = new TrainCartsLocationObjective(instruction, loc, range);
        objective.registerLocationEvents(service);
        return objective;
    }
}
