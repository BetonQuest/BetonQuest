package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Location;

/**
 * Factory for creating {@link LocationObjective} instances from {@link Instruction}s.
 */
public class LocationObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the LocationObjectiveFactory.
     */
    public LocationObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final FlagArgument<Boolean> entry = instruction.bool().getFlag("entry", true);
        final FlagArgument<Boolean> exit = instruction.bool().getFlag("exit", true);
        final LocationObjective objective = new LocationObjective(service, loc, range, entry, exit);
        objective.registerLocationEvents(service);
        return objective;
    }
}
