package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.TagData;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A tag changer that will add specified tags.
 */
public class AddTagChanger implements TagChanger {

    /**
     * Tags to add to the player.
     */
    private final Variable<List<String>> tags;

    /**
     * Create the tag changer that adds tags.
     *
     * @param tags tags to add
     */
    public AddTagChanger(final Variable<List<String>> tags) {
        this.tags = tags;
    }

    @Override
    public void changeTags(final TagData tagData, @Nullable final Profile profile) throws QuestException {
        for (final String tag : tags.getValue(profile)) {
            tagData.addTag(tag);
        }
    }
}
