package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final LocationObjective objective = new LocationObjective(service, loc, range);
        objective.registerLocationEvents(service);
        return objective;
    }
}
