package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * A condition that checks if a player has a certain scoreboard tag.
 */
public class ScoreboardTagCondition implements OnlineCondition {

    /**
     * The tag to check for.
     */
    private final Variable<String> tag;

    /**
     * Constructor for the tag condition.
     *
     * @param tag the tag to check for
     */
    public ScoreboardTagCondition(final Variable<String> tag) {
        this.tag = tag;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getScoreboardTags().contains(tag.getValue(profile));
    }
}
