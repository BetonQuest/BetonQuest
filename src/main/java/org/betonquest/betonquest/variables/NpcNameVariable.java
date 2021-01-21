package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * This variable resolves into the name of the NPC.
 */
@SuppressWarnings("PMD.CommentRequired")
public class NpcNameVariable extends Variable {

    public NpcNameVariable(final Instruction instruction) {
        super(instruction);
    }

    @Override
    public String getValue(final String playerID) {
        final Conversation conv = Conversation.getConversation(playerID);
        if (conv == null) {
            return "";
        }
        return conv.getData().getQuester(BetonQuest.getInstance().getPlayerData(playerID).getLanguage());
    }

}
