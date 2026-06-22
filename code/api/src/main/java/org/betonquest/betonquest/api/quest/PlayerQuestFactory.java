package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.jetbrains.annotations.Contract;

/**
 * Factory to create a specific {@link T}.
 *
 * @param <T> quest type requiring a player for execution
 * @since 3.0.0
 */
@FunctionalInterface
public interface PlayerQuestFactory<T> {

    /**
     * Parses an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse
     * @return {@link T} represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null -> new")
    T parsePlayer(Instruction instruction) throws QuestException;
}
