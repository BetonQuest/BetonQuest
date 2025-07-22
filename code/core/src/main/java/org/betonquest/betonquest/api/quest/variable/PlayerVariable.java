package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Interface for quest-variables that are checked for a profile. It represents the normal variable as described in the
 * BetonQuest user documentation. It does not represent the playerless variant though, see {@link PlayerlessVariable}.
 */
@FunctionalInterface
public interface PlayerVariable {
    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this variable
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(Profile profile) throws QuestException;
}
