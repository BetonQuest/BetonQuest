package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;

/**
 * Factory for creating {@link RegionObjective} instances from {@link Instruction}s.
 */
public class RegionObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the RegionObjectiveFactory.
     */
    public RegionObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final RegionObjective objective = new RegionObjective(instruction, name);
        objective.registerLocationEvents(service);
        return objective;
    }
}
