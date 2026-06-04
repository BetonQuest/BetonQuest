package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Citizens wrapper to get a Npc.
 */
public class CitizensWrapper implements NpcWrapper<NPC> {

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Id of the Npc.
     */
    private final Argument<Number> npcId;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param registry the registry of NPCs to use
     * @param npcId    the id of the Npc, greater or equals to zero
     */
    public CitizensWrapper(final NPCRegistry registry, final Argument<Number> npcId) {
        this.registry = registry;
        this.npcId = npcId;
    }

    @Override
    public Npc<NPC> getNpc(@Nullable final Profile profile) throws QuestException {
        final int npcId = this.npcId.getValue(profile).intValue();
        final NPC npc = registry.getById(npcId);
        if (npc == null) {
            throw new QuestException("Citizens NPC with ID '%d' not found".formatted(npcId));
        }
        return new CitizensAdapter(npc);
    }

    @Override
    public Set<Npc<NPC>> getNpcs(@Nullable final Profile profile) throws QuestException {
        return Set.of(getNpc(profile));
    }
}
