package org.betonquest.betonquest.compatibility.traincarts.objectives;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().atLeast(1).get("range", 1);
        final FlagArgument<Boolean> entry = instruction.bool().getFlag("entry", true);
        final FlagArgument<Boolean> exit = instruction.bool().getFlag("exit", true);
        final TrainCartsLocationObjective objective = new TrainCartsLocationObjective(service, loc, range, entry, exit);
        objective.registerLocationEvents(service);
        return objective;
    }
}
