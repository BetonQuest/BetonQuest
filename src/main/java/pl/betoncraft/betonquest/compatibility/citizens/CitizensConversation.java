package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import pl.betoncraft.betonquest.conversation.Conversation;

/**
 * Represents a conversation with NPC
 */
public class CitizensConversation extends Conversation {

    private final NPC npc;

    public CitizensConversation(final String playerID, final String conversationID, final Location location, final NPC npc) {
        super(playerID, conversationID, location);
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
