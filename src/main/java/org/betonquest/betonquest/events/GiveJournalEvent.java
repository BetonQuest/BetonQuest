package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Gives journal to the player.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class GiveJournalEvent extends QuestEvent {
    public GiveJournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
    }

    @Override
    protected Void execute(final String playerID) {
        BetonQuest.getInstance().getPlayerData(playerID).getJournal().addToInv();
        return null;
    }

}
