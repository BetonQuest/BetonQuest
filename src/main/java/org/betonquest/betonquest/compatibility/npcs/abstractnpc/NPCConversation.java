package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with NPC.
 *
 * @param <T> the original npc type
 */
public class NPCConversation<T> extends Conversation {
    /**
     * NPC used in this Conversation.
     */
    private final BQNPCAdapter<T> npc;

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param npc            the NPC used for this conversation
     */
    public NPCConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                           final Location center, final BQNPCAdapter<T> npc) {
        super(log, onlineProfile, conversationID, center);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation.
     *
     * @return the NPC Adapter
     */
    public BQNPCAdapter<T> getNPC() {
        return npc;
    }
}
