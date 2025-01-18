package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create if-else events from {@link Instruction}s.
 */
public class IfElseEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The empty constructor.
     */
    public IfElseEventFactory() {
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    private NullableEventAdapter createIfElseEvent(final Instruction instruction) throws QuestException {
        final ConditionID condition = instruction.getID(ConditionID::new);
        final EventID event = instruction.getID(EventID::new);
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final EventID elseEvent = instruction.getID(EventID::new);
        return new NullableEventAdapter(new IfElseEvent(condition, event, elseEvent));
    }
}
