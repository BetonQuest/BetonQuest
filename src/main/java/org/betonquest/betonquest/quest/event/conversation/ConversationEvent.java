package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;

/**
 * Starts a conversation.
 */
public class ConversationEvent implements Event {
    /**
     * The {@link BetonQuestLoggerFactory} to use for creating {@link BetonQuestLogger} instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The conversation to start.
     */
    private final ConversationID conversation;

    /**
     * Creates a new ConversationEvent.
     *
     * @param conversation the conversation to start
     */
    public ConversationEvent(final BetonQuestLoggerFactory loggerFactory, final ConversationID conversation) {
        this.loggerFactory = loggerFactory;
        this.conversation = conversation;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final OnlineProfile onlineProfile = profile.getOnlineProfile().get();
        new Conversation(loggerFactory.create(Conversation.class), onlineProfile, conversation, onlineProfile.getPlayer().getLocation());
    }
}
