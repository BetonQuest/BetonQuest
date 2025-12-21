package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.Variable;
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<PlayerResourcePackStatusEvent.Status> targetStatus =
                instruction.get(DefaultArgumentParsers.forEnumeration(PlayerResourcePackStatusEvent.Status.class));
        return new ResourcepackObjective(instruction, targetStatus);
    }
}
