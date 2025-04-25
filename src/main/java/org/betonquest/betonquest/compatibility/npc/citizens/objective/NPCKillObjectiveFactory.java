package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

import java.util.function.Predicate;

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
        final Instruction npcInstruction = instruction.getID(NpcID::new).getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        final String argument = npcInstruction.getPart(1);
        final Predicate<NPC> predicate;
        if (npcInstruction.hasArgument("byName")) {
            predicate = npc -> argument.equals(npc.getName());
        } else {
            final int npcId = npcInstruction.getInt(argument, -1);
            predicate = npc -> npcId == npc.getId();
        }
        final Variable<Number> targetAmount = instruction.getVariable(instruction.getOptional("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        return new NPCKillObjective(instruction, targetAmount, predicate);
    }
}
