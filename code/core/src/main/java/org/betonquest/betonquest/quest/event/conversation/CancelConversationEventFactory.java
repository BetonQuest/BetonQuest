package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * Factory to create conversation cancel events from {@link Instruction}s.
 */
public class CancelConversationEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the conversation cancel event factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param conversationApi the Conversation API
     */
    public CancelConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) {
        return new OnlineEventAdapter(
                new CancelConversationEvent(conversationApi),
                loggerFactory.create(CancelConversationEvent.class),
                instruction.getPackage()
        );
    }
}
