package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create conversation cancel events from {@link Instruction}s.
 */
public class CancelConversationEventFactory implements PlayerActionFactory {

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
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(
                new CancelConversationEvent(conversationApi),
                loggerFactory.create(CancelConversationEvent.class),
                instruction.getPackage()
        );
    }
}
