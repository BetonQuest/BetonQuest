package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link Variable} from {@link Instruction}s.
 */
public interface VariableFactory extends QuestFactory<Variable> {
    /**
     * Parses an instruction to create a normal {@link Variable}.
     *
     * @param instruction instruction to parse
     * @return variable represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    Variable parse(Instruction instruction) throws InstructionParseException;
}
