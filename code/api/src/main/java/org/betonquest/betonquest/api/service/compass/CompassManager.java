package org.betonquest.betonquest.api.service.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.compass.QuestCompass;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.profile.Profile;

import java.util.Map;

/**
 * The CompassManager is responsible for managing quest compasses.
 *
 * @since 3.0.0
 */
public interface CompassManager {

    /**
     * Returns the compass for the given identifier.
     *
     * @param identifier the identifier of the compass to retrieve
     * @return the compass
     * @throws QuestException if the compass cannot be accessed
     * @since 3.0.0
     */
    QuestCompass get(CompassIdentifier identifier) throws QuestException;

    /**
     * Returns the active compasses for the given profile.
     *
     * @param profile the profile to retrieve the compasses for
     * @return a map of identifiers to active compasses for the profile
     * @since 3.0.0
     */
    Map<CompassIdentifier, QuestCompass> forProfile(Profile profile);
}
