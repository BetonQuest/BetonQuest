package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * Citizens wrapper to get a Npc.
 */
class CitizensWrapper implements NpcWrapper<NPC> {
    /**
     * Id of the Npc.
     */
    private final Variable<Number> npcId;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param npcId the id of the Npc, greater or equals to zero
     */
    public CitizensWrapper(final Variable<Number> npcId) {
        this.npcId = npcId;
    }

    @Override
    public Npc<NPC> getNpc(@Nullable final Profile profile) throws QuestException {
        final int npcId = this.npcId.getValue(profile).intValue();
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " not found");
        }
        return new CitizensAdapter(npc);
    }
}
