package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.EventID;

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createFirstEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createFirstEvent(instruction);
    }

    private NullableEventAdapter createFirstEvent(final Instruction instruction) throws QuestException {
        final List<EventID> list = instruction.getList(instruction::getEvent);
        return new NullableEventAdapter(new FirstEvent(list));
    }
}
