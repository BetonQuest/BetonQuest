package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;

/**
 * Defines changes to be done on tags.
 */
@FunctionalInterface
public interface TagChanger {

    /**
     * Apply the changes to the defined tags.
     *
     * @param tagData Tag data whose tags shall be changed.
     */
    void changeTags(TagData tagData);
}
