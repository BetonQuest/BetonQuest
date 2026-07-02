package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryExtractEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Factory for {@link BrewDistillObjective}.
 *
 * @param api the TheBrewingProject API
 */
public record BrewDistillObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> distillRunsArgument = instruction.number().atLeast(1).get();
        final BrewDistillObjective objective = new BrewDistillObjective(service, api.getBrewManager(), distillRunsArgument);
        service.request(DistilleryExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(DistilleryExtractEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
