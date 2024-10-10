package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A condition to check if a player has a specified pointer in his journal.
 */
public class JournalCondition implements PlayerCondition {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * The target pointer to the journal to check for.
     */
    private final String targetPointer;

    /**
     * Create a new journal condition.
     *
     * @param betonQuest    the BetonQuest instance
     * @param targetPointer the target pointer to the journal to check for
     */
    public JournalCondition(final BetonQuest betonQuest, final String targetPointer) {
        this.betonQuest = betonQuest;
        this.targetPointer = targetPointer;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        for (final Pointer pointer : betonQuest.getPlayerData(profile).getJournal().getPointers()) {
            if (pointer.getPointer().equalsIgnoreCase(targetPointer)) {
                return true;
            }
        }
        return false;
    }
}
