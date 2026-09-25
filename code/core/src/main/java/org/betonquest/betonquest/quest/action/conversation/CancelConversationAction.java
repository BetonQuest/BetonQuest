package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Cancels the conversation.
 */
public class CancelConversationAction implements OnlineAction {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Whether the interceptor delay should be skipped.
     */
    private final FlagArgument<Boolean> skipDelay;

    /**
     * Create a new conversation cancel action with skipDelay flag.
     *
     * @param conversations the Conversation API
     * @param skipDelay     whether to skip the interceptor delay
     */
    public CancelConversationAction(final Conversations conversations, final FlagArgument<Boolean> skipDelay) {
        this.conversations = conversations;
        this.skipDelay = skipDelay;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        conversations.cancel(profile, skipDelay.getValue(profile).orElse(false));
    }
}
