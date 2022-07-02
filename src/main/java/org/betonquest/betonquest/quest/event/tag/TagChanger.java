package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.PlayerData;

/**
 * Defines changes to be done on tags.
 */
public interface TagChanger {

    /**
     * Apply the changes to the defined tags.
     */
    void changeTags(PlayerData playerData);
}
