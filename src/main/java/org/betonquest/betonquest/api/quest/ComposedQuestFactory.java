package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.ComposedCondition;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link T}.
 *
 * @param <T> {@link ComposedCondition}, {@literal ComposedEvent} or {@literal ComposedVariable}
 */
public interface ComposedQuestFactory<T> {
    /**
     * Parses an instruction to create a composed {@link T}.
     *
     * @param instruction instruction to parse
     * @return composed {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parseComposed(Instruction instruction) throws InstructionParseException;
}
