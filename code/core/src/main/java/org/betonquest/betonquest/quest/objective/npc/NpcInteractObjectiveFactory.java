package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.quest.objective.interact.Interaction;
import org.bukkit.event.EventPriority;

import static org.betonquest.betonquest.quest.objective.interact.Interaction.RIGHT;

/**
 * Factory for creating {@link NpcInteractObjective} instances from {@link Instruction}s.
 */
public class NpcInteractObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the NpcInteractObjectiveFactory.
     */
    public NpcInteractObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<NpcIdentifier> npcId = instruction.identifier(NpcIdentifier.class).get();
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<Interaction> interactionType = instruction.enumeration(Interaction.class).get("interaction", RIGHT);
        final NpcInteractObjective objective = new NpcInteractObjective(service, npcId, cancel, interactionType);
        service.request(NpcInteractEvent.class).priority(EventPriority.LOWEST).handler(objective::onNPCLeftClick)
                .profile(NpcInteractEvent::getProfile).subscribe(true);
        return objective;
    }
}
