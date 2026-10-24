package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Factory to create conversation cancel actions from {@link Instruction}s.
 */
public class CancelConversationActionFactory implements PlayerActionFactory {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create the conversation cancel action factory.
     *
     * @param conversations the Conversation API
     */
    public CancelConversationActionFactory(final Conversations conversations) {
        this.conversations = conversations;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final FlagArgument<Boolean> skipDelay = instruction.bool().getFlag("skipDelay", true);
        return new OnlineActionAdapter(new CancelConversationAction(conversations, skipDelay));
    }
}
