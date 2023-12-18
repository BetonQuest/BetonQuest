package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.NullStaticEventAdapter;

import java.util.List;

/**
 * Factory to create FirstEvents from events from {@link Instruction}s.
 */
public class FirstEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Empty constructor.
     */
    public FirstEventFactory() {

    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final List<EventID> list = instruction.getList(instruction::getEvent);

        return new FirstEvent(list);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new NullStaticEventAdapter(parseEvent(instruction));
    }
}
