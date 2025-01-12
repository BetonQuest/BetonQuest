package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * A condition that checks if a player has a certain tag.
 */
public class TagCondition implements PlayerCondition {

    /**
     * The tag to check for.
     */
    private final String tag;

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * Constructor for the tag condition.
     *
     * @param tag        the tag to check for
     * @param betonQuest the BetonQuest instance
     */
    public TagCondition(final String tag, final BetonQuest betonQuest) {
        this.tag = tag;
        this.betonQuest = betonQuest;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return betonQuest.getPlayerData(profile).hasTag(tag);
    }
}
