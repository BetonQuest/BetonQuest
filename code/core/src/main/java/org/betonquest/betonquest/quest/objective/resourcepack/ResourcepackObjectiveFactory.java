package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Factory for creating {@link ResourcepackObjective} instances from {@link Instruction}s.
 */
public class ResourcepackObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the ResourcepackObjectiveFactory.
     */
    public ResourcepackObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<PlayerResourcePackStatusEvent.Status> targetStatus =
                instruction.enumeration(PlayerResourcePackStatusEvent.Status.class).get();
        final ResourcepackObjective objective = new ResourcepackObjective(service, targetStatus);
        service.request(PlayerResourcePackStatusEvent.class).onlineHandler(objective::onResourcePackReceived)
                .player(PlayerResourcePackStatusEvent::getPlayer).subscribe(false);
        return objective;
    }
}
