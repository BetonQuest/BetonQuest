package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create {@link QuestEvent}s.
 */
public interface EventFactory {
    /**
     * Parse an instruction to create an event.
     *
     * @param instruction instruction to parse for the event
     * @return event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    QuestEvent parseEventInstruction(Instruction instruction) throws InstructionParseException;
}
