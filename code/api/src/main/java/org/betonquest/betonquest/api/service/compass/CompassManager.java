package org.betonquest.betonquest.api.service.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.compass.QuestCompass;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.profile.Profile;

import java.util.Map;

/**
 * The compass manager is responsible for managing the quest compasses.
 *
 * @since 3.0.0
 */
public interface CompassManager {

    /**
     * Get the compass for the given identifier.
     *
     * @param identifier the identifier of the compass to get
     * @return the compass
     * @throws QuestException if the compass could not be accessed
     * @since 3.0.0
     */
    QuestCompass get(CompassIdentifier identifier) throws QuestException;

    /**
     * Get the active compasses for the given profile.
     *
     * @param profile the profile to get the compasses for
     * @return all active compasses for the profile
     * @since 3.0.0
     */
    Map<CompassIdentifier, QuestCompass> forProfile(Profile profile);
}
