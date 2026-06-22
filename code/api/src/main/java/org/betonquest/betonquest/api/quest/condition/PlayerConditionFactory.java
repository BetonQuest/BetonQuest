package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerCondition} from {@link Instruction}s.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface PlayerConditionFactory extends PlayerQuestFactory<PlayerCondition> {

    /**
     * Parses an instruction to create a {@link PlayerCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     * @since 3.0.0
     */
    @Override
    PlayerCondition parsePlayer(Instruction instruction) throws QuestException;
}
