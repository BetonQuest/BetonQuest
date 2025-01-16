package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create a specific {@link T}.
 * <p>
 * Opposed to the {@link PlayerQuestFactory} it is used without a
 * {@link org.betonquest.betonquest.api.profiles.Profile Profile}.
 *
 * @param <T> quest type executed without a player
 */
public interface PlayerlessQuestFactory<T> {
    /**
     * Parses an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse
     * @return {@link T} represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    T parsePlayerless(Instruction instruction) throws QuestException;
}
