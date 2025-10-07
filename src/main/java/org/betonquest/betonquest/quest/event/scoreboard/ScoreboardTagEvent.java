package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * Adds or removes a scoreboard tag.
 */
public class ScoreboardTagEvent implements OnlineEvent {

    /**
     * The tag to add or remove.
     */
    private final Variable<String> tag;

    /**
     * Whether to add or remove the tag.
     */
    private final Variable<ScoreboardTagAction> action;

    /**
     * Create a new scoreboard tag event that adds or removes the given tag.
     *
     * @param tag    the tag to add or remove
     * @param action whether to add or remove the tag
     */
    public ScoreboardTagEvent(final Variable<String> tag, final Variable<ScoreboardTagAction> action) {
        this.tag = tag;
        this.action = action;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        action.getValue(profile).execute(profile, tag.getValue(profile));
    }
}
