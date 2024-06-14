package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link T}.
 *
 * @param <T> quest type requiring an online player for execution
 */
public interface OnlinePlayerQuestFactory<T> {
    /**
     * Parses an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse
     * @return {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parseOnlinePlayer(Instruction instruction) throws InstructionParseException;
}
