package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents a conversation with NPC
 */
@SuppressWarnings("PMD.CommentRequired")
public class CitizensConversation extends Conversation {

    private final NPC npc;

    public CitizensConversation(final BetonQuestLogger log, final OnlineProfile onlineProfile, final ConversationID conversationID, final Location location, final NPC npc) {
        super(log, onlineProfile, conversationID, location);
        this.npc = npc;
    }

    /**
     * This will return the NPC associated with this conversation only after the
     * coversation is created (all player options are listed and ready to
     * receive player input)
     *
     * @return the NPC or null if it's too early
     */
    public NPC getNPC() {
        return npc;
    }

}
