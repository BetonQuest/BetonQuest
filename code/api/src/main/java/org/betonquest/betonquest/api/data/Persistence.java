package org.betonquest.betonquest.api.data;

import org.betonquest.betonquest.api.profile.Profile;

/**
 * Represents all persistent states in BetonQuest.
 */
public interface Persistence {

    /**
     * Gets the global persistent data holder.
     *
     * @return the global persistent data holder
     */
    PersistentDataHolder global();

    /**
     * Gets the persistent data holder for the given profile.
     *
     * @param profile the profile to get the data holder for
     * @return the persistent data holder for the profile
     */
    PersistentDataHolder profile(Profile profile);
}
