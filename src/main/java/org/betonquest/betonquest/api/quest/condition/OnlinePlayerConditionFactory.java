package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.OnlinePlayerQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create specific {@link OnlinePlayerCondition} from {@link Instruction}s.
 */
public interface OnlinePlayerConditionFactory extends OnlinePlayerQuestFactory<OnlinePlayerCondition> {
    /**
     * Parses an instruction to create a {@link OnlinePlayerCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    OnlinePlayerCondition parseOnlinePlayer(Instruction instruction) throws InstructionParseException;
}
