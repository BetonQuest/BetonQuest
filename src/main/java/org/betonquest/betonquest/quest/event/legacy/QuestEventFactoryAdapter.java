package org.betonquest.betonquest.quest.event.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Adapter to let {@link EventFactory EventFactories} create {@link QuestEvent}s from the {@link Event}s and
 * {@link StaticEvent}s they create.
 */
public class QuestEventFactoryAdapter implements QuestEventFactory {

    /**
     * The event factory to be adapted.
     */
    private final EventFactory factory;

    /**
     * Create the factory from an {@link EventFactory}.
     *
     * @param factory factory to use
     */
    public QuestEventFactoryAdapter(final EventFactory factory) {
        this.factory = factory;
    }

    @Override
    public QuestEventAdapter parseEventInstruction(final Instruction instruction) throws InstructionParseException {
        final Event event = factory.parseEvent(instruction.copy());
        final StaticEvent staticEvent = factory.parseStaticEvent(instruction.copy());
        return new QuestEventAdapter(instruction, event, staticEvent);
    }
}
