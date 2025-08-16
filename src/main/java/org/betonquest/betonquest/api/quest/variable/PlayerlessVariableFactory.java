package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Factory to create a specific {@link PlayerlessVariable} from {@link Instruction}s.
 */
@FunctionalInterface
public interface PlayerlessVariableFactory extends PlayerlessQuestFactory<PlayerlessVariable> {
    /**
     * Parses an instruction to create a {@link PlayerlessVariable}.
     *
     * @param instruction instruction to parse
     * @return variable represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerlessVariable parsePlayerless(Instruction instruction) throws QuestException;
}
