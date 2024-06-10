package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link ComposedCondition} from {@link Instruction}s.
 */
public interface ComposedConditionFactory extends ComposedQuestFactory<ComposedCondition> {
    /**
     * Parses an instruction to create a {@link ComposedCondition}.
     *
     * @param instruction instruction to parse
     * @return composed condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    ComposedCondition parseComposed(Instruction instruction) throws InstructionParseException;
}
