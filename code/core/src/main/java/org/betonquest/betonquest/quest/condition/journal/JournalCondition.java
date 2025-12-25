package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.betonquest.betonquest.id.JournalEntryID;

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
    private final Argument<JournalEntryID> targetPointer;

    /**
     * Create a new journal condition.
     *
     * @param dataStorage   the storage providing player data
     * @param targetPointer the target pointer to the journal to check for
     */
    public JournalCondition(final PlayerDataStorage dataStorage, final Argument<JournalEntryID> targetPointer) {
        this.dataStorage = dataStorage;
        this.targetPointer = targetPointer;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final JournalEntryID resolved = targetPointer.getValue(profile);
        for (final Pointer pointer : dataStorage.get(profile).getEntries()) {
            if (pointer.pointer().equals(resolved)) {
                return true;
            }
        }
        return false;
    }
}
