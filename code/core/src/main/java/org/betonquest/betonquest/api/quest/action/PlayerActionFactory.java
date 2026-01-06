package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerAction} from {@link Instruction}s.
 */
@FunctionalInterface
public interface PlayerActionFactory extends PlayerQuestFactory<PlayerAction> {

    /**
     * Parses an instruction to create a {@link PlayerAction}.
     *
     * @param instruction instruction to parse
     * @return normal action represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerAction parsePlayer(Instruction instruction) throws QuestException;
}
