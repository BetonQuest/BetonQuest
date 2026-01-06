package org.betonquest.betonquest.quest.event.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * Adds or removes a scoreboard tag.
 */
public class ScoreboardTagEvent implements OnlineAction {

    /**
     * The tag to add or remove.
     */
    private final Argument<String> tag;

    /**
     * Whether to add or remove the tag.
     */
    private final Argument<ScoreboardTagOperation> action;

    /**
     * Create a new scoreboard tag event that adds or removes the given tag.
     *
     * @param tag    the tag to add or remove
     * @param action whether to add or remove the tag
     */
    public ScoreboardTagEvent(final Argument<String> tag, final Argument<ScoreboardTagOperation> action) {
        this.tag = tag;
        this.action = action;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        action.getValue(profile).execute(profile, tag.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
