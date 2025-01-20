package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
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
    public boolean check(final Profile profile) throws QuestException {
        final ConversationData conversation = BetonQuest.getInstance().getConversation(conversationID);
        if (conversation == null) {
            throw new QuestException("Tried to check conversation '" + conversationID.getFullID()
                    + "' but it is not loaded! Ensure it was loaded without errors.");
        }
        try {
            return conversation.isReady(profile);
        } catch (final ObjectNotFoundException e) {
            throw new QuestException(e.getMessage(), e);
        }
    }
}
