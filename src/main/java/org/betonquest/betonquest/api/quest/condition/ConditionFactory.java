package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link Condition} from {@link Instruction}s.
 */
public interface ConditionFactory {
    /**
     * Parses an instruction to create a normal {@link Condition}.
     *
     * @param instruction instruction to parse
     * @return normal condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    Condition parseCondition(Instruction instruction) throws InstructionParseException;
}
