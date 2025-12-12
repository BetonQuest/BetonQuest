package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;

/**
 * Factory to create Conversation IO for a conversation and online profile.
 */
@FunctionalInterface
public interface ConversationIOFactory {
    /**
     * Create the Conversation IO.
     *
     * @param conversation  the conversation to display
     * @param onlineProfile the player to show the conversation
     * @return the created conversation IO
     * @throws QuestException when the creation fails
     */
    ConversationIO parse(Conversation conversation, OnlineProfile onlineProfile) throws QuestException;
}
