package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link PlayerVariable} from {@link Instruction}s.
 */
public interface VariableFactory extends QuestFactory<PlayerVariable> {
    /**
     * Parses an instruction to create a normal {@link PlayerVariable}.
     *
     * @param instruction instruction to parse
     * @return variable represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    PlayerVariable parse(Instruction instruction) throws InstructionParseException;
}
