package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.HybridEventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * HybridEventFactory that will provide both {@link Event} and {@link StaticEvent} implementations
 * from the created {@link org.betonquest.betonquest.api.quest.event.HybridEvent HybridEvent}.
 */
public abstract class StandardHybridEventFactory implements HybridEventFactory, EventFactory, StaticEventFactory {
    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return parseHybridEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return parseHybridEvent(instruction);
    }
}
