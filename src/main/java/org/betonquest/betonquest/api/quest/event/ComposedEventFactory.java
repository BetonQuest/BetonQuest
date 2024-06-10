package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link ComposedEvent} from {@link Instruction}s.
 */
public interface ComposedEventFactory extends QuestFactory<ComposedEvent> {
    /**
     * Parses an instruction to create a {@link ComposedEvent}.
     *
     * @param instruction instruction to parse
     * @return event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    ComposedEvent parseComposedEvent(Instruction instruction) throws InstructionParseException;

    @Override
    default ComposedEvent parse(final Instruction instruction) throws InstructionParseException {
        return parseComposedEvent(instruction);
    }
}
