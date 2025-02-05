package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Citizens wrapper to get a Npc.
 */
public class CitizensNameWrapper implements NpcWrapper<NPC> {
    /**
     * Name of the Npc.
     */
    private final String npcName;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param npcName the name of the Npc
     */
    public CitizensNameWrapper(final String npcName) {
        this.npcName = npcName;
    }

    @Override
    public Npc<NPC> getNpc() throws QuestException {
        final List<NPC> npcsWithName = new ArrayList<>();
        for (final NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.getName().equals(npcName)) {
                if (npcsWithName.isEmpty()) {
                    npcsWithName.add(npc);
                } else {
                    throw new QuestException("Multiple NPCs with the same name: " + npcName);
                }
            }
        }
        if (npcsWithName.isEmpty()) {
            throw new QuestException("NPC with name '" + npcName + "' not found");
        }
        return new CitizensAdapter(npcsWithName.get(0));
    }
}
