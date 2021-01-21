package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.LogUtils;

import java.util.logging.Level;

/**
 * Gives journal to the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class GiveJournalEvent extends QuestEvent {

    private int journalSlot;

    public GiveJournalEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        try {
            journalSlot = Integer.parseInt(Config.getString("config.default_journal_slot"));
        } catch (NumberFormatException e) {
            LogUtils.getLogger().log(Level.WARNING, "Could not read default_journal_slot: " + e.getMessage());
            LogUtils.logThrowable(e);
            journalSlot = -1;
        }
    }

    @Override
    protected Void execute(final String playerID) {
        BetonQuest.getInstance().getPlayerData(playerID).getJournal().addToInv(journalSlot);
        return null;
    }

}
