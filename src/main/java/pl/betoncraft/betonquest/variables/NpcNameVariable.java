package pl.betoncraft.betonquest.variables;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.conversation.Conversation;

/**
 * This variable resolves into the name of the NPC.
 */
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
