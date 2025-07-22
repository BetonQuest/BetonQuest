package org.betonquest.betonquest.quest.event.conversation;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory for {@link ConversationEvent}.
 */
public class ConversationEventFactory implements PlayerEventFactory {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create the conversation event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param data          the data for primary server thread access
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data,
                                    final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Pair<ConversationID, String>> conversation = getConversation(instruction);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ConversationEvent(loggerFactory, pluginMessage, conversation),
                loggerFactory.create(ConversationEvent.class),
                instruction.getPackage()
        ), data);
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction to get option name from
     * @return the conversation ID and the option name as a pair
     * @throws QuestException if no NPC option with the given name is present
     */
    private Variable<Pair<ConversationID, String>> getConversation(final Instruction instruction) throws QuestException {
        final String conversation = instruction.next();
        final String option = instruction.getValue("option", "");
        return instruction.get(conversation + " " + option, combined -> {
            final String[] split = combined.split(" ");
            final ConversationID conversationID = new ConversationID(instruction.getPackage(), split[0]);
            final String optionName = split.length == 2 ? split[1] : null;
            if (optionName != null) {
                final String optionPath = "conversations." + conversationID.getBaseID() + ".NPC_options." + optionName;
                if (!conversationID.getPackage().getConfig().contains(optionPath)) {
                    throw new QuestException("NPC Option '" + optionName + "' does not exist in '" + conversationID + "'.");
                }
            }
            return Pair.of(conversationID, optionName);
        });
    }
}
