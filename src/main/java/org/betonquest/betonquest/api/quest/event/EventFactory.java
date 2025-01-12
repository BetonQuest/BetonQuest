package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Factory to create a specific {@link Event} from {@link Instruction}s.
 */
public interface EventFactory extends PlayerQuestFactory<Event> {
    /**
     * Parses an instruction to create a normal {@link Event}.
     *
     * @param instruction instruction to parse
     * @return normal event represented by the instruction
     * @throws QuestException when the instruction cannot be parsed
     */
    Event parseEvent(Instruction instruction) throws QuestException;

    @Override
    default Event parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvent(instruction);
    }
}
