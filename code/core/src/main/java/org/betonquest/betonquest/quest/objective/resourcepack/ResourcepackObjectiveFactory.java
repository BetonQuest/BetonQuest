package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<PlayerResourcePackStatusEvent.Status> targetStatus =
                instruction.enumeration(PlayerResourcePackStatusEvent.Status.class).get();
        return new ResourcepackObjective(instruction, targetStatus);
    }
}
