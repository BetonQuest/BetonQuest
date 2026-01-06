package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.npc.NpcInteractEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
    public DefaultObjective parseInstruction(final Instruction instruction, final ObjectiveFactoryService service) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(NpcID::new).get();
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<Interaction> interactionType = instruction.enumeration(Interaction.class).get("interaction", RIGHT);
        final NpcInteractObjective objective = new NpcInteractObjective(instruction, npcId, cancel, interactionType);
        service.request(NpcInteractEvent.class).priority(EventPriority.LOWEST).handler(objective::onNPCLeftClick)
                .profile(NpcInteractEvent::getProfile).subscribe(true);
        return objective;
    }
}
