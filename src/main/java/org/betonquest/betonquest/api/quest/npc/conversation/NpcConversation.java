package org.betonquest.betonquest.api.quest.npc.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with Npc.
 *
 * @param <T> the original npc type
 */
public class NpcConversation<T> extends Conversation {
    /**
     * Npc used in this Conversation.
     */
    private final Npc<T> npc;

    /**
     * Starts a new conversation between player and npc at given location.
     *
     * @param log            the logger that will be used for logging
     * @param onlineProfile  the profile of the player
     * @param conversationID ID of the conversation
     * @param center         location where the conversation has been started
     * @param npc            the Npc used for this conversation
     */
    public NpcConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                           final Location center, final Npc<T> npc) {
        super(log, onlineProfile, conversationID, center);
        this.npc = npc;
    }

    /**
     * This will return the Npc associated with this conversation.
     *
     * @return the BetonQuest Npc
     */
    public Npc<T> getNPC() {
        return npc;
    }
}
