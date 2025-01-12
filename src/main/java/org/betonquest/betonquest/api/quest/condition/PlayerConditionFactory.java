package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Factory to create a specific {@link PlayerCondition} from {@link Instruction}s.
 */
public interface PlayerConditionFactory extends PlayerQuestFactory<PlayerCondition> {
    /**
     * Parses an instruction to create a {@link PlayerCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerCondition parsePlayer(Instruction instruction) throws QuestException;
}
