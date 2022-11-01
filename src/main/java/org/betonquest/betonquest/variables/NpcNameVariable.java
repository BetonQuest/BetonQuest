package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
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
    public String getValue(final Profile profile) {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv == null) {
            return "";
        }
        return conv.getData().getQuester(BetonQuest.getInstance().getPlayerData(profile).getLanguage());
    }

}
