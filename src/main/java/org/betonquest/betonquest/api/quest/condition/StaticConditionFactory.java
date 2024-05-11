package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link StaticCondition} from {@link Instruction}s.
 */
public interface StaticConditionFactory {
    /**
     * Parses an instruction to create a {@link StaticCondition}.
     *
     * @param instruction instruction to parse
     * @return "static" condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    StaticCondition parseStaticCondition(Instruction instruction) throws InstructionParseException;
}
