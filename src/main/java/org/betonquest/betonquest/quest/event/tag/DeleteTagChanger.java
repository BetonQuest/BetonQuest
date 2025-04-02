package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.TagData;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A tag changer that will remove specified tags.
 */
public class DeleteTagChanger implements TagChanger {

    /**
     * Tags to remove from the player.
     */
    private final List<VariableIdentifier> tags;

    /**
     * Create the tag changer that removes tags.
     *
     * @param tags tags to remove
     */
    public DeleteTagChanger(final List<VariableIdentifier> tags) {
        this.tags = tags;
    }

    @Override
    public void changeTags(final TagData tagData, @Nullable final Profile profile) throws QuestException {
        for (final VariableIdentifier tag : tags) {
            tagData.removeTag(tag.getValue(profile));
        }
    }
}
