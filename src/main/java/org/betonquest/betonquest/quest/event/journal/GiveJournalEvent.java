package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Gives journal to the player.
 */
public class GiveJournalEvent implements Event {

    /**
     * Creates a new GiveJournalEvent.
     */
    public GiveJournalEvent() {
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        BetonQuest.getInstance().getPlayerData(profile).getJournal().addToInv();
    }
}
