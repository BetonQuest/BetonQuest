package org.betonquest.betonquest.quest.event.chat;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

/**
 * The chat event factory.
 */
public class ChatEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the chat event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public ChatEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) {
        final String[] messages = String.join(" ", instruction.getValueParts()).split("\\|");
        return new OnlineEventAdapter(new ChatEvent(messages),
                loggerFactory.create(ChatEvent.class),
                instruction.getPackage());
    }
}
