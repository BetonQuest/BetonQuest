package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A condition that checks if a player has a certain tag.
 */
public class GlobalTagCondition implements PlayerlessCondition {

    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * The tag to check for.
     */
    private final String tag;

    /**
     * Constructor for the tag condition.
     *
     * @param betonQuest the BetonQuest instance
     * @param tag        the tag to check for
     */
    public GlobalTagCondition(final BetonQuest betonQuest, final String tag) {
        this.betonQuest = betonQuest;
        this.tag = tag;
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        return betonQuest.getGlobalData().hasTag(tag);
    }
}
