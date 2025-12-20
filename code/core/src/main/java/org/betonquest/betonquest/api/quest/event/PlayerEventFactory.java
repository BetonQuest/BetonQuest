package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;

/**
 * Factory to create a specific {@link PlayerEvent} from {@link DefaultInstruction}s.
 */
@FunctionalInterface
public interface PlayerEventFactory extends PlayerQuestFactory<PlayerEvent> {

    /**
     * Parses an instruction to create a {@link PlayerEvent}.
     *
     * @param instruction instruction to parse
     * @return normal event represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    @Override
    PlayerEvent parsePlayer(DefaultInstruction instruction) throws QuestException;
}
