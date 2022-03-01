package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Event that does nothing. This class implements the null object pattern.
 */
public class NullEvent extends QuestEvent {
    /**
     * Create a null event. Refer to {@link QuestEvent#QuestEvent(Instruction, boolean)} for possible modes of failure.
     *
     * @param instruction the instruction will be mostly ignored
     * @throws InstructionParseException when the instruction contains severe errors
     */
    public NullEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        return null;
    }
}
