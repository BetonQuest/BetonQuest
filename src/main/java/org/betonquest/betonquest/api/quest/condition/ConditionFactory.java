package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link Condition} from {@link Instruction}s.
 */
public interface ConditionFactory extends QuestFactory<Condition> {
    /**
     * Parses an instruction to create a normal {@link Condition}.
     *
     * @param instruction instruction to parse
     * @return normal condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    Condition parse(Instruction instruction) throws InstructionParseException;
}
