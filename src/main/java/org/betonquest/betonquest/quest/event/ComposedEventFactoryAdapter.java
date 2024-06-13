package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.QuestTypeAdapter;

/**
 * Factory adapter for that will provide both {@link Event} and {@link StaticEvent} implementations
 * from the supplied {@link ComposedEventFactory}.
 */
public class ComposedEventFactoryAdapter extends QuestTypeAdapter<ComposedEvent, Event, StaticEvent> implements EventFactory, StaticEventFactory {
    /**
     * Create a new ComposedEventFactoryAdapter to create {@link Event}s and {@link StaticEvent}s from it.
     *
     * @param composedEventFactory the factory used to parse the instruction.
     */
    public ComposedEventFactoryAdapter(final QuestFactory<ComposedEvent> composedEventFactory) {
        super(composedEventFactory);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return factory.parse(instruction);
    }
}
