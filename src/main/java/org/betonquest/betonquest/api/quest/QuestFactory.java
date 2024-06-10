package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link T}.
 *
 * @param <T> {@literal Condition}, {@literal ComposedEvent} or {@literal ComposedVariable}
 */
public interface QuestFactory<T> {
    /**
     * Parses an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse
     * @return {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parse(Instruction instruction) throws InstructionParseException;
}
