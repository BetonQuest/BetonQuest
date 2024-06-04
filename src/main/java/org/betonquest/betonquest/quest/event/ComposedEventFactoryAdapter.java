package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory adapter for that will provide both {@link Event} and {@link StaticEvent} implementations
 * from the supplied {@link ComposedEventFactory}.
 */
public class ComposedEventFactoryAdapter implements EventFactory, StaticEventFactory {
    /**
     * Composed event factory used to create new events and static events.
     */
    private final ComposedEventFactory composedEventFactory;

    /**
     * Create a new ComposedEventFactoryAdapter to create {@link Event}s and {@link StaticEvent}s from it.
     *
     * @param composedEventFactory the factory used to parse the instruction.
     */
    public ComposedEventFactoryAdapter(final ComposedEventFactory composedEventFactory) {
        this.composedEventFactory = composedEventFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return composedEventFactory.parseComposedEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return composedEventFactory.parseComposedEvent(instruction);
    }
}
