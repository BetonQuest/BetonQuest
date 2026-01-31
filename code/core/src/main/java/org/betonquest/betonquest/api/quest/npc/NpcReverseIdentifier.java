package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Allows Integrations to identify to a {@link Npc} associated {@link NpcIdentifier}s.
 */
public interface NpcReverseIdentifier {

    /**
     * Gets the instruction strings which could be used to identify the Npc.
     *
     * @param npc     the Npc to get its identifier from
     * @param profile the related profile allowing finer selection decision
     * @return all ids this thing can identify the npc
     */
    Set<NpcIdentifier> getIdsFromNpc(Npc<?> npc, @Nullable OnlineProfile profile);

    /**
     * Adds an ID to be possible used to identify npcs.
     *
     * @param npcId       the id to register
     * @param instruction the resolved instruction string
     */
    void addID(NpcIdentifier npcId, String instruction);

    /**
     * Resets all possible ID links.
     */
    void reset();
}
