package org.betonquest.betonquest.quest.event.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.conversation.ConversationID;

/**
 * Factory for {@link ConversationEvent}.
 */
public class ConversationEventFactory implements PlayerEventFactory {

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
    public ConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                    final ConversationApi conversationApi) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.conversationApi = conversationApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Pair<ConversationID, String>> conversation = getConversation(instruction);
        return new OnlineEventAdapter(new ConversationEvent(conversationApi, conversation),
                loggerFactory.create(ConversationEvent.class), instruction.getPackage());
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction to get option name from
     * @return the conversation ID and the option name as a pair
     * @throws QuestException if no NPC option with the given name is present
     */
    private Variable<Pair<ConversationID, String>> getConversation(final Instruction instruction) throws QuestException {
        final String conversation = instruction.nextElement();
        final String option = instruction.string().get("option", "").getValue(null);
        return instruction.get(conversation + " " + option, combined -> {
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
        });
    }
}
