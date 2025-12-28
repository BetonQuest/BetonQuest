package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensArgument;

/**
 * Factory for creating {@link NPCKillObjective} instances from {@link Instruction}s.
 */
public class NPCKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Creates a new instance of the NPCKillObjectiveFactory.
     *
     * @param registry the registry of NPCs to use
     */
    public NPCKillObjectiveFactory(final NPCRegistry registry) {
        this.registry = registry;
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<NpcID> npcID = instruction.parse(CitizensArgument.CITIZENS_ID).get();
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);
        return new NPCKillObjective(instruction, registry, targetAmount, npcID);
    }
}
