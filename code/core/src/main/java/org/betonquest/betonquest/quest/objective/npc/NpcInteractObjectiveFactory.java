package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.quest.objective.interact.Interaction;

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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcId = instruction.parse(NpcID::new).get();
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", false);
        final Argument<Interaction> interactionType = instruction.enumeration(Interaction.class).get("interaction", RIGHT);
        return new NpcInteractObjective(instruction, npcId, cancel, interactionType);
    }
}
