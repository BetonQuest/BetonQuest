package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.BrewConsumeEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;

/**
 * Factory for {@link BrewConsumeObjective}.
 *
 * @param api the TheBrewingProject API
 */
public record BrewConsumeObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> brewType = instruction.string().get();
        final BrewQualityArgument brewQualityArgument = BrewQualityArgument.parseInstructions(instruction);
        final BrewConsumeObjective objective = new BrewConsumeObjective(service, api.getBrewManager(), brewQualityArgument, brewType);
        service.request(BrewConsumeEvent.class)
                .onlineHandler(objective::handle)
                .player(BrewConsumeEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
