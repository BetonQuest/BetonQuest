package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create a specific {@link PlayerVariable} from {@link Instruction}s.
 */
public interface PlayerVariableFactory extends PlayerQuestFactory<PlayerVariable> {
    /**
     * Parses an instruction to create a {@link PlayerVariable}.
     *
     * @param instruction instruction to parse
     * @return variable represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerVariable parsePlayer(Instruction instruction) throws QuestException;
}
