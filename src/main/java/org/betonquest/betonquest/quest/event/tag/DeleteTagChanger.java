package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.TagData;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.jetbrains.annotations.Nullable;

/**
 * A tag changer that will remove specified tags.
 */
public class DeleteTagChanger implements TagChanger {

    /**
     * Tags to remove from the player.
     */
    private final VariableList<String> tags;

    /**
     * Create the tag changer that removes tags.
     *
     * @param tags tags to remove
     */
    public DeleteTagChanger(final VariableList<String> tags) {
        this.tags = tags;
    }

    @Override
    public void changeTags(final TagData tagData, @Nullable final Profile profile) throws QuestException {
        for (final String tag : tags.getValue(profile)) {
            tagData.removeTag(tag);
        }
    }
}
