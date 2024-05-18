package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link HybridEvent} from {@link Instruction}s.
 */
public interface HybridEventFactory {
    /**
     * Parses an instruction to create a {@link HybridEvent}.
     *
     * @param instruction instruction to parse
     * @return hybrid event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    HybridEvent parseHybridEvent(Instruction instruction) throws InstructionParseException;
}
