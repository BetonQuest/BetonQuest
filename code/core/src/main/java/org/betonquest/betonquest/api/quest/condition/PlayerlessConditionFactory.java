package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;

/**
 * Factory to create a specific {@link PlayerlessCondition} from {@link DefaultInstruction}s.
 */
@FunctionalInterface
public interface PlayerlessConditionFactory extends PlayerlessQuestFactory<PlayerlessCondition> {

    /**
     * Parses an instruction to create a {@link PlayerlessCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerlessCondition parsePlayerless(DefaultInstruction instruction) throws QuestException;
}
