package org.betonquest.betonquest.conversation.io;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

/**
 * Tellraw conversation output.
 */
public class TellrawConvIOFactory implements ConversationIOFactory {
    /**
     * The colors used for the conversation.
     */
    private final ConversationColors colors;

    /**
     * Create a new Tellraw conversation IO factory.
     *
     * @param colors the colors used for the conversation
     */
    public TellrawConvIOFactory(final ConversationColors colors) {
        this.colors = colors;
    }

    @Override
    public ConversationIO parse(final Conversation conversation, final OnlineProfile onlineProfile) {
        return new TellrawConvIO(conversation, onlineProfile, colors);
    }
}
