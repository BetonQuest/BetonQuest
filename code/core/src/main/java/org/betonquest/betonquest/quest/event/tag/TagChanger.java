package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.database.TagData;
import org.jetbrains.annotations.Nullable;

/**
 * Defines changes to be done on tags.
 */
@FunctionalInterface
public interface TagChanger {

    /**
     * Apply the changes to the defined tags.
     *
     * @param tagData Tag data whose tags shall be changed.
     * @param profile Profile of the player whose tags shall be changed.
     * @throws QuestException If the tag data cannot be changed.
     */
    void changeTags(TagData tagData, @Nullable Profile profile) throws QuestException;
}
