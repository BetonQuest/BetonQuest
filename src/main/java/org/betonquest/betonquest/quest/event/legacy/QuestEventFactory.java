package org.betonquest.betonquest.quest.event.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create {@link QuestEvent}s from {@link Instruction}s.
 */
public interface QuestEventFactory {
    /**
     * Parse an instruction to create a {@link QuestEvent}.
     *
     * @param instruction instruction to parse for the event
     * @return event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    QuestEvent parseEventInstruction(Instruction instruction) throws InstructionParseException;
}
