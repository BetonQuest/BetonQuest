package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.api.quest.event.HybridEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;

/**
 * Factory to create ifelse events from {@link Instruction}s.
 */
public class IfElseEventFactory implements HybridEventFactory {

    /**
     * The empty constructor
     */
    public IfElseEventFactory() {
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.PrematureDeclaration"})
    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final ConditionID condition = instruction.getCondition();
        final EventID event = instruction.getEvent();
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new InstructionParseException("Missing 'else' keyword");
        }
        final EventID elseEvent = instruction.getEvent();
        return new IfElseEvent(condition, event, elseEvent);
    }
}
