package org.betonquest.betonquest.quest.condition.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create {@link Condition Legacy Condition}s from {@link Instruction}s
 */
public interface LegacyConditionFactory {
    /**
     * Parse an instruction to create a {@link Condition}.
     *
     * @param instruction instruction to parse for the condition
     * @return condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    Condition parseConditionInstruction(Instruction instruction) throws InstructionParseException;
}
