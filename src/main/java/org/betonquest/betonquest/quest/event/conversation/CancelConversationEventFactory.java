package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new OnlineEventAdapter(
                new CancelConversationEvent(),
                loggerFactory.create(CancelConversationEvent.class),
                instruction.getPackage()
        );
    }
}
