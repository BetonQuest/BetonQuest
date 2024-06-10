package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link T}.
 * <p>
 * Opposed to the {@link PlayerQuestFactory PlayerQuestFactories'} {@code T} it is used without a
 * {@link org.betonquest.betonquest.api.profiles.Profile Profile}.
 *
 * @param <T> {@link PlayerlessCondition}, {@link StaticEvent} or playerless variable
 */
public interface PlayerlessQuestFactory<T> {
    /**
     * Parses an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse
     * @return {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parsePlayerless(Instruction instruction) throws InstructionParseException;
}
