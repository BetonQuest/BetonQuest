package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.event.DrunkEventInitiateEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Factory for {@link DrunkenEventObjective}.
 */
public record DrunkenEventObjectiveFactory() implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> eventArgument = instruction.string().get();
        final DrunkenEventObjective objective = new DrunkenEventObjective(service, eventArgument);
        service.request(DrunkEventInitiateEvent.class)
                .onlineHandler(objective::handle)
                .player(DrunkEventInitiateEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
