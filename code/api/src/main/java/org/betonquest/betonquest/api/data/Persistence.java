package org.betonquest.betonquest.api.data;

import org.betonquest.betonquest.api.profile.Profile;

/**
 * Represents all persistent states in BetonQuest.
 *
 * @since 3.0.0
 */
public interface Persistence {

    /**
     * Gets the global persistent data holder.
     *
     * @return the global persistent data holder
     * @since 3.0.0
     */
    PersistentDataHolder global();

    /**
     * Gets the persistent data holder for the given profile.
     *
     * @param profile the profile to get the data holder for
     * @return the persistent data holder for the profile
     * @since 3.0.0
     */
    @SuppressWarnings("PMD.ShortMethodName")
    PersistentDataHolder of(Profile profile);
}
