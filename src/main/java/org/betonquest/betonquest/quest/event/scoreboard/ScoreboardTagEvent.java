package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

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
    private final boolean remove;

    /**
     * Create a new scoreboard tag event that adds or removes the given tag.
     *
     * @param tag    the tag to add or remove
     * @param remove whether to add or remove the tag
     */
    public ScoreboardTagEvent(final String tag, final boolean remove) {
        this.tag = tag;
        this.remove = remove;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        if (remove) {
            profile.getPlayer().removeScoreboardTag(tag);
        } else {
            profile.getPlayer().addScoreboardTag(tag);
        }
    }
}
