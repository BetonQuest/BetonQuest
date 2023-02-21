package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.NullStaticEventAdapter;

/**
 * Factory to create ifelse events from {@link Instruction}s.
 */
public class IfElseEventFactory implements EventFactory, StaticEventFactory {

    /**
     * The empty constructor
     */
    public IfElseEventFactory() {
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.PrematureDeclaration"})
    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final ConditionID condition = instruction.getCondition();
        final EventID event = instruction.getEvent();
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new InstructionParseException("Missing 'else' keyword");
        }
        final EventID elseEvent = instruction.getEvent();
        return new IfElseEvent(condition, event, elseEvent);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new NullStaticEventAdapter(parseEvent(instruction));
    }
}
