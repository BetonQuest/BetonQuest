package org.betonquest.betonquest.quest.event.conversation;

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
import org.jetbrains.annotations.Nullable;

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
        final String conversation = instruction.next();
        final Variable<ConversationID> conversationID = instruction.get(conversation, ConversationID::new);
        final Variable<String> startingOption = getStartOption(instruction, conversation);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ConversationEvent(loggerFactory, pluginMessage, conversationID, startingOption),
                loggerFactory.create(ConversationEvent.class),
                instruction.getPackage()
        ), data);
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction  to get option name from
     * @param conversation to get option from
     * @return null if no option argument is given, otherwise the option name
     * @throws QuestException if no NPC option with the given name is present
     */
    @Nullable
    private Variable<String> getStartOption(final Instruction instruction, final String conversation) throws QuestException {
        final String option = instruction.getValue("option");
        if (option == null) {
            return null;
        }
        return instruction.get(conversation + " " + option, combined -> {
            final String[] split = combined.split(" ");
            final ConversationID conversationID = new ConversationID(instruction.getPackage(), split[0]);
            final String optionName = split[1];
            final String optionPath = "conversations." + conversationID.getBaseID() + ".NPC_options." + optionName;
            if (!conversationID.getPackage().getConfig().contains(optionPath)) {
                throw new QuestException("NPC Option '" + optionName + "' does not exist in '" + conversationID + "'.");
            }
            return optionName;
        });
    }
}
