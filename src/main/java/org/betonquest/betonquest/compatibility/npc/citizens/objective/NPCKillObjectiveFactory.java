package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link NPCKillObjective} instances from {@link Instruction}s.
 */
public class NPCKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the NPCKillObjectiveFactory.
     */
    public NPCKillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Variable<NpcID> npcID = instruction.get(CitizensArgument.CITIZENS_ID);
        final Variable<Number> targetAmount = instruction.getValue("amount", Argument.NUMBER_NOT_LESS_THAN_ONE, 1);
        return new NPCKillObjective(instruction, targetAmount, npcID);
    }
}
