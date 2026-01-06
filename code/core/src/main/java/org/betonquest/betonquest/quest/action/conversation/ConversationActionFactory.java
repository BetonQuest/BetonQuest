package org.betonquest.betonquest.quest.action.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * Factory for {@link ConversationAction}.
 */
public class ConversationActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Conversation API.
     */
    private final ConversationApi conversationApi;

    /**
     * Create the conversation event factory.
     *
     * @param loggerFactory   the logger factory to create a logger for the events
     * @param packManager     the quest package manager to get quest packages from
     * @param conversationApi the Conversation API
     */
    public ConversationActionFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                     final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Pair<ConversationID, String>> conversation = getConversation(instruction);
        return new OnlineActionAdapter(new ConversationAction(conversationApi, conversation),
                loggerFactory.create(ConversationAction.class), instruction.getPackage());
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction to get option name from
     * @return the conversation ID and the option name as a pair
     * @throws QuestException if no NPC option with the given name is present
     */
    private Argument<Pair<ConversationID, String>> getConversation(final Instruction instruction) throws QuestException {
        final String conversation = instruction.nextElement();
        final String option = instruction.string().get("option", "").getValue(null);
        return instruction.chainForArgument(conversation + " " + option).parse(combined -> {
            final String[] split = combined.split(" ");
            final ConversationID conversationID = new ConversationID(packManager, instruction.getPackage(), split[0]);
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
