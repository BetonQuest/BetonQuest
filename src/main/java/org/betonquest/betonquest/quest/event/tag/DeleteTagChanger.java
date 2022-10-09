package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;

import java.util.Arrays;

/**
 * A tag changer that will remove specified tags.
 */
public class DeleteTagChanger implements TagChanger {

    /**
     * Tags to remove from the player.
     */
    private final String[] tags;

    /**
     * Create the tag changer that removes tags.
     *
     * @param tags tags to remove
     */
    public DeleteTagChanger(final String... tags) {
        this.tags = Arrays.copyOf(tags, tags.length);
    }

    @Override
    public void changeTags(final TagData tagData) {
        for (final String tag : tags) {
            tagData.removeTag(tag);
        }
    }
}
