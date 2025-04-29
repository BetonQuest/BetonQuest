package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.instruction.Instruction;
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
        final ConversationID conversationID = new ConversationID(instruction.getPackage(), instruction.next());
        final String startingOption = getStartOption(instruction, conversationID);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ConversationEvent(loggerFactory, pluginMessage, conversationID, startingOption),
                loggerFactory.create(ConversationEvent.class),
                instruction.getPackage()
        ), data);
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction    to get option name from
     * @param conversationID to get option from
     * @return null if no option argument is given, otherwise the option name
     * @throws QuestException if no NPC option with the given name is present
     */
    @Nullable
    private String getStartOption(final Instruction instruction, final ConversationID conversationID) throws QuestException {
        final String targetOptionName = instruction.getValue("option");
        if (targetOptionName == null) {
            return null;
        }

        // We need to manually check the existence of the starting option because the conversation is not loaded yet.
        final String optionPath = "conversations." + conversationID.getBaseID() + ".NPC_options." + targetOptionName;
        if (!conversationID.getPackage().getConfig().contains(optionPath)) {
            throw new QuestException("NPC Option '" + targetOptionName + "' does not exist in '" + conversationID + "'.");
        }

        return targetOptionName;
    }
}
