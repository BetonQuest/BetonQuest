package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Interface for quest-conditions that are checked for a profile. It represents the normal condition as described in the
 * BetonQuest user documentation. It does not represent the playerless variant though, see {@link PlayerlessCondition}.
 */
public interface PlayerCondition {
    /**
     * Checks the condition.
     *
     * @param profile the {@link Profile} the condition is checked for
     * @return if the condition is fulfilled
     * @throws QuestException when the condition check fails
     */
    boolean check(Profile profile) throws QuestException;
}
