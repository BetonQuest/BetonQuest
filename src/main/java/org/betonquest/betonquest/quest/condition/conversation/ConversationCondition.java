package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;

/**
 * Checks if the conversation with player has at least one possible option.
 */
public class ConversationCondition implements PlayerCondition {

    /**
     * The conversation to check.
     */
    private final ConversationID conversationID;

    /**
     * Creates a new ConversationCondition.
     *
     * @param conversationID the conversation to check
     */
    public ConversationCondition(final ConversationID conversationID) {
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        try {
            final ConversationData conversation = BetonQuest.getInstance().getConversation(conversationID);
            if (conversation == null) {
                throw new QuestRuntimeException("Tried to check conversation '" + conversationID.getFullID()
                        + "' but it is not loaded! Check for errors on /bq reload!");
            }
            return conversation.isReady(profile);
        } catch (InstructionParseException | ObjectNotFoundException e) {
            throw new QuestRuntimeException("External pointers in the conversation this condition checks for could not"
                    + " be resoled during runtime.", e);
        }
    }
}
