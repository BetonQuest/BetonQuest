package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.data.TagHolder;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * The playerless tag action, doing what was defined in its instruction.
 */
public class PlayerlessTagAction implements PlayerlessAction {

    /**
     * The tagHolder that shall be tagged.
     */
    private final TagHolder tagHolder;

    /**
     * Tags changer that will add or remove the defined tags.
     */
    private final TagChanger tagChanger;

    /**
     * Create a playerless tag action.
     *
     * @param tagHolder  the tagData that shall be tagged.
     * @param tagChanger changes the defined tags
     */
    public PlayerlessTagAction(final TagHolder tagHolder, final TagChanger tagChanger) {

        this.tagHolder = tagHolder;
        this.tagChanger = tagChanger;
    }

    @Override
    public void execute() throws QuestException {
        tagChanger.changeTags(tagHolder, null);
    }
}
