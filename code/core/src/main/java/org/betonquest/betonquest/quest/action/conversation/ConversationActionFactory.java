package org.betonquest.betonquest.quest.action.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConversationIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;

/**
 * Factory for {@link ConversationAction}.
 */
public class ConversationActionFactory implements PlayerActionFactory {

    /**
     * Conversation API.
     */
    private final Conversations conversations;

    /**
     * Create the conversation action factory.
     *
     * @param conversations the Conversation API
     */
    public ConversationActionFactory(final Conversations conversations) {
        this.conversations = conversations;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Pair<ConversationIdentifier, String>> conversation = getConversation(instruction);
        return new OnlineActionAdapter(new ConversationAction(conversations, conversation));
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction to get option name from
     * @return the conversation ID and the option name as a pair
     * @throws QuestException if no NPC option with the given name is present
     */
    private Argument<Pair<ConversationIdentifier, String>> getConversation(final Instruction instruction) throws QuestException {
        final String conversation = instruction.nextElement();
        final String option = instruction.string().get("option", "").getValue(null);
        return instruction.chainForArgument(conversation + " " + option).parse(combined -> {
            final String[] split = combined.split(" ");
            final ConversationIdentifier conversationID = instruction.chainForArgument(split[0]).identifier(ConversationIdentifier.class).get().getValue(null);
            final String optionName = split.length == 2 ? split[1] : null;
            if (optionName != null) {
                final String optionPath = "conversations." + conversationID.get() + ".NPC_options." + optionName;
                if (!conversationID.getPackage().getConfig().contains(optionPath)) {
                    throw new QuestException("NPC Option '" + optionName + "' does not exist in '" + conversationID + "'.");
                }
            }
            return Pair.of(conversationID, optionName);
        }).get();
    }
}
