package org.betonquest.betonquest.compatibility.npcs.abstractnpc;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a conversation with NPC.
 */
public abstract class NPCConversation extends Conversation {
    /**
     * NPC used in this Conversation.
     */
    private final BQNPCAdapter npc;

    /**
     * {@inheritDoc}
     *
     * @param npc the NPC used for this conversation
     */
    public NPCConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                           final Location center, final BQNPCAdapter npc) {
        super(log, onlineProfile, conversationID, center);
        this.npc = npc;
    }

    /**
     * {@inheritDoc}
     *
     * @param npc the NPC used for this conversation
     */
    public NPCConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID,
                           final Location center, @Nullable final String startingOption, final BQNPCAdapter npc) {
        super(log, onlineProfile, conversationID, center, startingOption);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation only after the
     * conversation is created (all player options are listed and ready to
     * receive player input)
     * TODO is this javadoc valid?
     *
     * @return the NPC or null if it's too early
     */
    public BQNPCAdapter getNPC() {
        return npc;
    }
}
