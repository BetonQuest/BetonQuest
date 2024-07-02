package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.OnlinePlayerQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create specific {@link OnlinePlayerEvent} from {@link Instruction}s.
 */
public interface OnlinePlayerEventFactory extends OnlinePlayerQuestFactory<OnlinePlayerEvent> {
    /**
     * Parses an instruction to create a {@link OnlinePlayerEvent}.
     *
     * @param instruction instruction to parse
     * @return event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    OnlinePlayerEvent parseOnlinePlayer(Instruction instruction) throws InstructionParseException;
}
