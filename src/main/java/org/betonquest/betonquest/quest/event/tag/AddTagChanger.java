package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.database.PlayerData;

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
     * @param tags tags to add
     */
    public AddTagChanger(String... tags) {
        this.tags = tags;
    }

    @Override
    public void changeTags(PlayerData playerData) {
        for (final String tag : tags) {
            playerData.addTag(tag);
        }
    }
}
