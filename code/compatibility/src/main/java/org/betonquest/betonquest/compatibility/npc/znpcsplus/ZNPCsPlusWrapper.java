package org.betonquest.betonquest.compatibility.npc.znpcsplus;

import lol.pyr.znpcsplus.api.npc.NpcEntry;
import lol.pyr.znpcsplus.api.npc.NpcRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * ZNPCsPlus wrapper to get a Npc.
 */
public class ZNPCsPlusWrapper implements NpcWrapper<NpcEntry> {

    /**
     * ZNPCsPlus Npc Registry.
     */
    private final NpcRegistry npcRegistry;

    /**
     * Npc identifier.
     */
    private final Variable<String> npcId;

    /**
     * Create a new FancyNpcs Npc Wrapper.
     *
     * @param npcRegistry the Npc Registry to get Npcs from
     * @param npcId       the npc identifier
     */
    public ZNPCsPlusWrapper(final NpcRegistry npcRegistry, final Variable<String> npcId) {
        this.npcRegistry = npcRegistry;
        this.npcId = npcId;
    }

    @Override
    public Npc<NpcEntry> getNpc(@Nullable final Profile profile) throws QuestException {
        final String npcId = this.npcId.getValue(profile);
        final NpcEntry npcEntry = npcRegistry.getById(npcId);
        if (npcEntry == null) {
            throw new QuestException("ZNPCsPlus Npc with ID " + npcId + " not found");
        }
        return new ZNPCsPlusAdapter(npcEntry);
    }
}
