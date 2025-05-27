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
public class CitizensNameWrapper implements NpcWrapper<NPC> {
    /**
     * Name of the Npc.
     */
    private final Variable<String> npcName;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param npcName the name of the Npc
     */
    public CitizensNameWrapper(final Variable<String> npcName) {
        this.npcName = npcName;
    }

    @Override
    public Npc<NPC> getNpc(@Nullable final Profile profile) throws QuestException {
        NPC selectedNpc = null;
        final String npcName = this.npcName.getValue(profile);
        for (final NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.getName().equals(npcName)) {
                if (selectedNpc == null) {
                    selectedNpc = npc;
                } else {
                    throw new QuestException("Multiple NPCs with the same name: " + npcName);
                }
            }
        }
        if (selectedNpc == null) {
            throw new QuestException("NPC with name '" + npcName + "' not found");
        }
        return new CitizensAdapter(selectedNpc);
    }
}
