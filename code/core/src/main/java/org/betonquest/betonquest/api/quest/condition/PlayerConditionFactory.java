package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerCondition} from {@link DefaultInstruction}s.
 */
@FunctionalInterface
public interface PlayerConditionFactory extends PlayerQuestFactory<PlayerCondition> {

    /**
     * Parses an instruction to create a {@link PlayerCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerCondition parsePlayer(DefaultInstruction instruction) throws QuestException;
}
