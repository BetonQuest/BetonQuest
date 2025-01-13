package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * A condition to check if a player has a specified pointer in his journal.
 */
public class JournalCondition implements OnlineCondition {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The target pointer to the journal to check for.
     */
    private final String targetPointer;

    /**
     * Create a new journal condition.
     *
     * @param dataStorage   the storage providing player data
     * @param targetPointer the target pointer to the journal to check for
     */
    public JournalCondition(final PlayerDataStorage dataStorage, final String targetPointer) {
        this.dataStorage = dataStorage;
        this.targetPointer = targetPointer;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        for (final Pointer pointer : dataStorage.get(profile).getJournal().getPointers()) {
            if (pointer.getPointer().equalsIgnoreCase(targetPointer)) {
                return true;
            }
        }
        return false;
    }
}
