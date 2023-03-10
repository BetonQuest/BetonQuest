package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Starts a conversation.
 */
public class ConversationEvent implements Event {

    /**
     * The conversation to start.
     */
    private final String conversation;

    /**
     * Creates a new ConversationEvent.
     *
     * @param conversation the conversation to start
     */
    public ConversationEvent(final String conversation) {
        this.conversation = conversation;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        new Conversation(onlineProfile, conversation, onlineProfile.getPlayer().getLocation());
    }
}
