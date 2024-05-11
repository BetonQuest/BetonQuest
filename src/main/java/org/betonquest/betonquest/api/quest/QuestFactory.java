package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.Condition;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link T}.
 *
 * @param <T> {@link Condition}, {@link Event}, objective or variable
 */
public interface QuestFactory<T> {
    /**
     * Parses an instruction to create a normal {@link T}.
     *
     * @param instruction instruction to parse
     * @return normal {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parse(Instruction instruction) throws InstructionParseException;
}
