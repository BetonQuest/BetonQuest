package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Adds or removes a scoreboard tag.
 */
public class ScoreboardTagEvent implements OnlineEvent {

    /**
     * The tag to add or remove.
     */
    private final String tag;

    /**
     * Whether to add or remove the tag.
     */
    private final ScoreboardTagAction action;

    /**
     * Create a new scoreboard tag event that adds or removes the given tag.
     *
     * @param tag    the tag to add or remove
     * @param action whether to add or remove the tag
     */
    public ScoreboardTagEvent(final String tag, final ScoreboardTagAction action) {
        this.tag = tag;
        this.action = action;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        action.execute(profile, tag);
    }
}
