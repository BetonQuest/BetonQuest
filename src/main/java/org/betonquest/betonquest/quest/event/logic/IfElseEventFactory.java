package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;

/**
 * Factory to create ifelse events from {@link Instruction}s.
 */
public class IfElseEventFactory implements ComposedEventFactory {

    /**
     * The empty constructor
     */
    public IfElseEventFactory() {
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.PrematureDeclaration"})
    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final ConditionID condition = instruction.getCondition();
        final EventID event = instruction.getEvent();
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new InstructionParseException("Missing 'else' keyword");
        }
        final EventID elseEvent = instruction.getEvent();
        return new IfElseEvent(condition, event, elseEvent);
    }
}
