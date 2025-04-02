package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.database.TagData;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A tag changer that will add specified tags.
 */
public class AddTagChanger implements TagChanger {

    /**
     * Tags to add to the player.
     */
    private final List<VariableIdentifier> tags;

    /**
     * Create the tag changer that adds tags.
     *
     * @param tags tags to add
     */
    public AddTagChanger(final List<VariableIdentifier> tags) {
        this.tags = tags;
    }

    @Override
    public void changeTags(final TagData tagData, @Nullable final Profile profile) throws QuestException {
        for (final VariableIdentifier tag : tags) {
            tagData.addTag(tag.getValue(profile));
        }
    }
}
