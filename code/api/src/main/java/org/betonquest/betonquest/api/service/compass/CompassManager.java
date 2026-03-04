package org.betonquest.betonquest.api.service.compass;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.compass.QuestCompass;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;

/**
 * The compass manager is responsible for managing the quest compasses.
 */
public interface CompassManager {

    /**
     * Get the compass for the given identifier.
     *
     * @param identifier the identifier of the compass to get
     * @return the compass
     * @throws QuestException if the compass could not be accessed
     */
    QuestCompass get(CompassIdentifier identifier) throws QuestException;
}
