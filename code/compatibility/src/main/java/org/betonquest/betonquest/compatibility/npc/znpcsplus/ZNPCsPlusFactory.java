package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;

/**
 * Factory to get ZNPCsPlus Npcs.
 */
public class ZNPCsPlusFactory implements NpcFactory {

    /**
     * ZNPCsPlus Npc Registry.
     */
    private final NpcRegistry npcRegistry;

    /**
     * The empty default constructor.
     *
     * @param npcRegistry the Npc Registry to get Npcs from
     */
    public ZNPCsPlusFactory(final NpcRegistry npcRegistry) {
        this.npcRegistry = npcRegistry;
    }

    @Override
    public NpcWrapper<NpcEntry> parseInstruction(final Instruction instruction) throws QuestException {
        return new ZNPCsPlusWrapper(npcRegistry, instruction.get(instruction.getParsers().string()));
    }
}
