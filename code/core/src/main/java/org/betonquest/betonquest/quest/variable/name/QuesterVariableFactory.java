package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;

/**
 * Factory to create {@link QuesterVariable}s from {@link Instruction}s.
 */
public class QuesterVariableFactory implements PlayerVariableFactory {
    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create a NpcName variable factory.
     *
     * @param conversationApi the Conversation API
     */
    public QuesterVariableFactory(final ConversationApi conversationApi) {
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) {
        return new QuesterVariable(conversationApi);
    }
}
