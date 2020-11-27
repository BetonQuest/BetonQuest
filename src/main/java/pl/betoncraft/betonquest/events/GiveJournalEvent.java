package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LogUtils;

import java.util.logging.Level;

/**
 * Gives journal to the player.
 */
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
