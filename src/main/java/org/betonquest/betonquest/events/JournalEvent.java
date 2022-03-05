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

    /**
     * Journal action applicable for one specific player.
     */
    private final EventAction journalAction;

    /**
     * Journal action applicable for all players.
     */
    private final EventBulkAction journalBulkAction;

    /**
     * Create JournalEvent from Instruction.
     *
     * @param instruction instruction to parse.
     * @throws InstructionParseException if the instruction contains errors
     */
    public JournalEvent(final Instruction instruction, final EventAction journalAction, final EventBulkAction journalBulkAction) throws InstructionParseException {
        super(instruction, false);
        this.journalAction = journalAction;
        this.journalBulkAction = journalBulkAction;
        staticness = true;
    }

    @Override
    protected Void execute(final String playerId) throws QuestRuntimeException {
        if (playerId == null) {
            journalBulkAction.doBulkAction();
        } else {
            journalAction.doAction(playerId);
        }
        return null;
    }
}
