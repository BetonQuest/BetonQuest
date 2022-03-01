package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.events.action.EventAction;
import org.betonquest.betonquest.events.action.EventBulkAction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Adds the entry to player's journal.
 */
@CustomLog
public class JournalEvent extends QuestEvent {

    private final EventAction journalAction;
    private final EventBulkAction eventBulkAction;

    /**
     * Create JournalEvent from Instruction.
     *
     * @param instruction instruction to parse.
     * @throws InstructionParseException if the instruction contains errors
     */
    public JournalEvent(final Instruction instruction, final EventAction journalAction, final EventBulkAction eventBulkAction) throws InstructionParseException {
        super(instruction, false);
        this.journalAction = journalAction;
        this.eventBulkAction = eventBulkAction;
        staticness = true;
    }

    @Override
    protected Void execute(final String playerId) throws QuestRuntimeException {
        if (playerId == null) {
            eventBulkAction.doBulkAction();
        } else {
            journalAction.doAction(playerId);
        }
        return null;
    }
}
