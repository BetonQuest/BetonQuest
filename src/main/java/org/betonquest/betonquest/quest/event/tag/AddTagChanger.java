package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.TagData;

import java.util.Arrays;

/**
 * A tag changer that will add specified tags.
 */
public class AddTagChanger implements TagChanger {

    /**
     * Tags to add to the player.
     */
    private final String[] tags;

    /**
     * Create the tag changer that adds tags.
     *
     * @param tags tags to add
     */
    public AddTagChanger(final String... tags) {
        this.tags = Arrays.copyOf(tags, tags.length);
    }

    @Override
    public void changeTags(final TagData tagData) {
        for (final String tag : tags) {
            tagData.addTag(tag);
        }
    }
}
