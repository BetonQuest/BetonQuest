package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Gives journal to the player.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class GiveJournalEvent extends QuestEvent {

    private int journalSlot;

    public GiveJournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        try {
            journalSlot = Integer.parseInt(Config.getString("config.default_journal_slot"));
        } catch (final NumberFormatException e) {
            LOG.warning(instruction.getPackage(), "Could not read default_journal_slot: " + e.getMessage(), e);
            journalSlot = -1;
        }
    }

    @Override
    protected Void execute(final String playerID) {
        BetonQuest.getInstance().getPlayerData(playerID).getJournal().addToInv(journalSlot);
        return null;
    }

}
