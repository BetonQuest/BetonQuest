package org.betonquest.betonquest.quest.objective.npc;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
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
        final Variable<NpcID> npcId = instruction.get(NpcID::new);
        final boolean cancel = instruction.hasArgument("cancel");
        final Variable<Interaction> interactionType = instruction.getValue("interaction", Argument.ENUM(Interaction.class), RIGHT);
        return new NpcInteractObjective(instruction, npcId, cancel, interactionType);
    }
}
