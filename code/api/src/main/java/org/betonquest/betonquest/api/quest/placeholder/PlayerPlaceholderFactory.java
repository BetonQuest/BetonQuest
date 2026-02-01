package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerPlaceholder} from {@link Instruction}s.
 */
@FunctionalInterface
public interface PlayerPlaceholderFactory extends PlayerQuestFactory<PlayerPlaceholder> {

    /**
     * Parses an instruction to create a {@link PlayerPlaceholder}.
     *
     * @param instruction instruction to parse
     * @return placeholder represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerPlaceholder parsePlayer(Instruction instruction) throws QuestException;
}
