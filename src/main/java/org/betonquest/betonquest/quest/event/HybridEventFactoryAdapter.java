package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.HybridEventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory adapter for that will provide both {@link Event} and {@link StaticEvent} implementations
 * from the supplied {@link HybridEventFactory}.
 */
public class HybridEventFactoryAdapter implements EventFactory, StaticEventFactory {
    /**
     * Hybrid event factory used to create new events and static events.
     */
    private final HybridEventFactory hybridEventFactory;

    /**
     * Create a new HybridEventFactoryAdapter to create {@link Event}s and {@link StaticEvent}s from it.
     *
     * @param hybridEventFactory the factory used to parse the instruction.
     */
    public HybridEventFactoryAdapter(final HybridEventFactory hybridEventFactory) {
        this.hybridEventFactory = hybridEventFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return hybridEventFactory.parseHybridEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return hybridEventFactory.parseHybridEvent(instruction);
    }
}
