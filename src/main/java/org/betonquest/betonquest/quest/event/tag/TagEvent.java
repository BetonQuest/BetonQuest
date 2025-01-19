package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.database.TagData;

import java.util.function.Function;

/**
 * The tag event, doing what was defined in its instruction.
 */
public class TagEvent implements Event {
    /**
     * Function providing the tagData for the given player.
     */
    private final Function<Profile, TagData> getTagData;

    /**
     * Tags changer that will add or remove the defined tags.
     */
    private final TagChanger tagChanger;

    /**
     * Create a tag event.
     *
     * @param getTagData function providing the tagData for the given player
     * @param tagChanger changes the defined tags
     */
    public TagEvent(final Function<Profile, TagData> getTagData, final TagChanger tagChanger) {
        this.getTagData = getTagData;
        this.tagChanger = tagChanger;
    }

    @Override
    public void execute(final Profile profile) {
        final TagData tagData = getTagData.apply(profile);
        tagChanger.changeTags(tagData);
    }
}
