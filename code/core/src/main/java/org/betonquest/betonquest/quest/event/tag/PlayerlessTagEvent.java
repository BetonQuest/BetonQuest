package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.database.TagData;

/**
 * The static tag event, doing what was defined in its instruction.
 */
public class PlayerlessTagEvent implements PlayerlessEvent {

    /**
     * Static tagData that shall be tagged.
     */
    private final TagData tagData;

    /**
     * Tags changer that will add or remove the defined tags.
     */
    private final TagChanger tagChanger;

    /**
     * Create a static tag event.
     *
     * @param tagData    Static tagData that shall be tagged.
     * @param tagChanger changes the defined tags
     */
    public PlayerlessTagEvent(final TagData tagData, final TagChanger tagChanger) {

        this.tagData = tagData;
        this.tagChanger = tagChanger;
    }

    @Override
    public void execute() throws QuestException {
        tagChanger.changeTags(tagData, null);
    }
}
