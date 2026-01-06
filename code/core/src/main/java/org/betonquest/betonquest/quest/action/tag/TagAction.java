package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.database.TagData;

import java.util.function.Function;

/**
 * The tag action, doing what was defined in its instruction.
 */
public class TagAction implements PlayerAction {

    /**
     * Function providing the tagData for the given player.
     */
    private final Function<Profile, TagData> getTagData;

    /**
     * Tags changer that will add or remove the defined tags.
     */
    private final TagChanger tagChanger;

    /**
     * Create a tag action.
     *
     * @param getTagData function providing the tagData for the given player
     * @param tagChanger changes the defined tags
     */
    public TagAction(final Function<Profile, TagData> getTagData, final TagChanger tagChanger) {
        this.getTagData = getTagData;
        this.tagChanger = tagChanger;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final TagData tagData = getTagData.apply(profile);
        tagChanger.changeTags(tagData, profile);
    }
}
