package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link HybridEvent} from {@link Instruction}s.
 * <p>
 * This factory will provide by default both {@link Event} and {@link StaticEvent} implementations
 * from the created {@link HybridEvent}.
 */
public interface HybridEventFactory extends EventFactory, StaticEventFactory {
    /**
     * Parses an instruction to create a {@link HybridEvent}.
     *
     * @param instruction instruction to parse
     * @return hybrid event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    HybridEvent parseHybridEvent(Instruction instruction) throws InstructionParseException;

    @Override
    default Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return parseHybridEvent(instruction);
    }

    @Override
    default StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return parseHybridEvent(instruction);
    }
}
