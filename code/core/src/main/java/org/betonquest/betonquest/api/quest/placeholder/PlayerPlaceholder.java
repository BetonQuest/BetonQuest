package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for quest-placeholders that are checked for a profile. It represents the normal placeholder as described
 * in the BetonQuest user documentation. It does not represent the playerless variant though, see
 * {@link PlayerlessPlaceholder}.
 */
@FunctionalInterface
public interface PlayerPlaceholder extends PrimaryThreadEnforceable {

    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this placeholder
     * @throws QuestException when the value could not be retrieved
     */
    String getValue(Profile profile) throws QuestException;
}
