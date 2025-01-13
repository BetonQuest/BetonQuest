package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The Feature logic.
 */
public interface FeatureAPI {

    /**
     * Gets stored Conversation Data.
     * <p>
     * The conversation data can be null if there was an error loading it.
     *
     * @param conversationID package name, dot and name of the conversation
     * @return ConversationData object for this conversation or null if it does not exist
     */
    @Nullable
    ConversationData getConversation(ConversationID conversationID);

    /**
     * Get the loaded Quest Canceller.
     *
     * @return quest cancellers in a new map
     */
    Map<QuestCancelerID, QuestCanceler> getCanceler();
}
