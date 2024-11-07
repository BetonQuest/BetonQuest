package org.betonquest.betonquest.compatibility.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with NPC
 */
public class FancyNpcsConversation extends Conversation {
    /**
     * NPC used in this Conversation.
     */
    private final Npc npc;

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the {@link OnlineProfile} of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param npc            the NPC used for this conversation
     */
    public FancyNpcsConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID, final Location center, final Npc npc) {
        super(log, onlineProfile, conversationID, center);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation.
     *
     * @return the NPC or null if it's too early
     */
    public Npc getNpc() {
        return npc;
    }
}
