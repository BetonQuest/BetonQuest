package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

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
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final FlagArgument<Boolean> entry = instruction.bool().getFlag("entry", true);
        final FlagArgument<Boolean> exit = instruction.bool().getFlag("exit", true);
        final RegionObjective objective = new RegionObjective(service, name, entry, exit);
        objective.registerLocationEvents(service);
        return objective;
    }
}
