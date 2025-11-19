package org.betonquest.betonquest.quest.event.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * Starts a conversation.
 */
public class ConversationEvent implements OnlineEvent {

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * The conversation to start.
     */
    private final Variable<Pair<ConversationID, String>> conversation;

    /**
     * Creates a new ConversationEvent.
     *
     * @param conversationApi the Conversation API
     * @param conversation    the conversation and option to start as a pair
     */
    public ConversationEvent(final ConversationApi conversationApi, final Variable<Pair<ConversationID, String>> conversation) {
        this.conversationApi = conversationApi;
        this.conversation = conversation;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Pair<ConversationID, String> conversation = this.conversation.getValue(profile);
        conversationApi.start(profile, conversation.getKey(), profile.getPlayer().getLocation(), conversation.getValue());
    }
}
