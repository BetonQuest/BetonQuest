package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Defines changes to be done on tags.
 */
@FunctionalInterface
public interface TagChanger {

    /**
     * Apply the changes to the defined tags.
     *
     * @param tagHolder the tagHolder whose tags shall be changed.
     * @param profile   Profile of the player whose tags shall be changed.
     * @throws QuestException If the tag data cannot be changed.
     */
    void changeTags(TagHolder tagHolder, @Nullable Profile profile) throws QuestException;
}
