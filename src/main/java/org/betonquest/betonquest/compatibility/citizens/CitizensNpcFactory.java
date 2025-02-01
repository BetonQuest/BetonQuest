package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.Set;

/**
 * Creates validated Npc Wrapper for Citizens Npcs.
 */
public class CitizensNpcFactory implements NpcFactory<NPC> {
    /**
     * The default Constructor.
     */
    public CitizensNpcFactory() {
    }

    @Override
    public NpcWrapper<NPC> parseInstruction(final Instruction instruction) throws QuestException {
        if (instruction.hasArgument("byName")) {
            return new CitizensNameWrapper(instruction.next());
        }
        final int npcId = instruction.getInt();
        if (npcId < 0) {
            throw new QuestException("NPC ID cannot be less than 0");
        }
        return new CitizensWrapper(npcId);
    }

    @Override
    public Set<String> npcInstructionStrings(final Npc<NPC> npc) {
        return Set.of(String.valueOf(npc.getOriginal().getId()), npc.getOriginal().getName());
    }

    @Override
    public Class<? extends Npc<NPC>> factoredClass() {
        return CitizensAdapter.class;
    }
}
