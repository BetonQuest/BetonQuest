package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;

/**
 * Factory to create a specific {@link PlayerlessAction} from {@link Instruction}s.
 */
@FunctionalInterface
public interface PlayerlessActionFactory extends PlayerlessQuestFactory<PlayerlessAction> {

    /**
     * Parses an instruction to create a {@link PlayerlessAction}.
     *
     * @param instruction instruction to parse
     * @return action represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerlessAction parsePlayerless(Instruction instruction) throws QuestException;
}
