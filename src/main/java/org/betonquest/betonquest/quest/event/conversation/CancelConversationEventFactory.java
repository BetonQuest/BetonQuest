package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;

/**
 * Factory to create conversation cancel events from {@link Instruction}s.
 */
public class CancelConversationEventFactory implements EventFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create the conversation cancel event factory.
     */
    public CancelConversationEventFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new OnlineProfileRequiredEvent(
                log, new CancelConversationEvent(), instruction.getPackage());
    }
}
