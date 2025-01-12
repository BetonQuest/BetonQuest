package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link ConversationEvent}.
 */
public class ConversationEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the conversation event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public ConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final ConversationID conversationID;
        try {
            conversationID = new ConversationID(instruction.getPackage(), instruction.next());
        } catch (final ObjectNotFoundException e) {
            throw new QuestException(e.getMessage(), e);
        }
        final String startingOption = getStartOption(instruction, conversationID);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ConversationEvent(loggerFactory, conversationID, startingOption),
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
        final String targetOptionName = instruction.getOptional("option");
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
