package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

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
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        BetonQuest.getInstance().getPlayerData(profile).getJournal().addToInv();
        return null;
    }

}
