package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Factory to create Objective Data.
 */
@FunctionalInterface
public interface ObjectiveDataFactory {

    /**
     * Create a new objective data object to persist objective progress.
     *
     * @param instruction the stored data instruction
     * @param profile     the profile the data is for
     * @param objID       the id of the objective the data is for
     * @return the newly created data object
     * @throws QuestException when the objective data could not be parsed
     */
    ObjectiveData create(String instruction, Profile profile, ObjectiveID objID) throws QuestException;
}
