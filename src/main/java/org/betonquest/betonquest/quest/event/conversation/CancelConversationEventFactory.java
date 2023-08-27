package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;

/**
 * Factory to create conversation cancel events from {@link Instruction}s.
 */
public class CancelConversationEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the conversation cancel event factory.
     *
     * @param loggerFactory logger factory to use
     */
    public CancelConversationEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new OnlineProfileRequiredEvent(
                loggerFactory.create(CancelConversationEvent.class), new CancelConversationEvent(), instruction.getPackage());
    }
}
