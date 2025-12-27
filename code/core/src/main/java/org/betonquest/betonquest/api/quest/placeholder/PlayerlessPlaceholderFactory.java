package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;

/**
 * Factory to create a specific {@link PlayerlessPlaceholder} from {@link Instruction}s.
 */
@FunctionalInterface
public interface PlayerlessPlaceholderFactory extends PlayerlessQuestFactory<PlayerlessPlaceholder> {

    /**
     * Parses an instruction to create a {@link PlayerlessPlaceholder}.
     *
     * @param instruction instruction to parse
     * @return placeholder represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerlessPlaceholder parsePlayerless(Instruction instruction) throws QuestException;
}
