package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.StandardHybridEventFactory;

import java.util.List;

/**
 * Factory to create FirstEvents from events from {@link Instruction}s.
 */
public class FirstEventFactory extends StandardHybridEventFactory {

    /**
     * Empty constructor.
     */
    public FirstEventFactory() {

    }

    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final List<EventID> list = instruction.getList(instruction::getEvent);

        return new FirstEvent(list);
    }
}
