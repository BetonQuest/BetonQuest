package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;

/**
 * Factory to create conversation cancel actions from {@link Instruction}s.
 */
public class CancelConversationActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the conversation cancel action factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the actions
     * @param conversationApi the Conversation API
     */
    public CancelConversationActionFactory(final BetonQuestLoggerFactory loggerFactory, final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) {
        return new OnlineActionAdapter(
                new CancelConversationAction(conversationApi),
                loggerFactory.create(CancelConversationAction.class),
                instruction.getPackage()
        );
    }
}
