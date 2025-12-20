package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerVariable} from {@link DefaultInstruction}s.
 */
@FunctionalInterface
public interface PlayerVariableFactory extends PlayerQuestFactory<PlayerVariable> {

    /**
     * Parses an instruction to create a {@link PlayerVariable}.
     *
     * @param instruction instruction to parse
     * @return variable represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerVariable parsePlayer(DefaultInstruction instruction) throws QuestException;
}
