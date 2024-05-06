package org.betonquest.betonquest.compatibility.npcs.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.BQNPCAdapter;
import org.betonquest.betonquest.compatibility.npcs.abstractnpc.NPCConversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with NPC
 */
public class CitizensConversation extends NPCConversation {
    /**
     * NPC used in this Conversation.
     */
    private final NPC npc;

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param npc            the NPC used for this conversation
     * @param adapter        the npc adapter
     */
    public CitizensConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID, final Location center, final NPC npc, final BQNPCAdapter adapter) {
        super(log, onlineProfile, conversationID, center, adapter);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation.
     *
     * @return the NPC or null if it's too early
     */
    public NPC getCitizensNPC() {
        return npc;
    }
}
