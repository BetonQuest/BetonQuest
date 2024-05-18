package org.betonquest.betonquest.api.quest.event;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.ComposedQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link ComposedEvent} from {@link Instruction}s.
 */
public interface ComposedEventFactory extends ComposedQuestFactory<ComposedEvent> {
    /**
     * Parses an instruction to create a {@link ComposedEvent}.
     *
     * @param instruction instruction to parse
     * @return composed event represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    ComposedEvent parseComposedEvent(Instruction instruction) throws InstructionParseException;

    @Override
    default ComposedEvent parseComposed(final Instruction instruction) throws InstructionParseException {
        return parseComposedEvent(instruction);
    }
}
