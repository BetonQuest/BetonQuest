package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * A condition that checks if a player has a certain tag.
 */
public class GlobalTagCondition implements PlayerlessCondition {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * The tag to check for.
     */
    private final Variable<String> tag;

    /**
     * Constructor for the tag condition.
     *
     * @param globalData the global data
     * @param tag        the tag to check for
     */
    public GlobalTagCondition(final GlobalData globalData, final Variable<String> tag) {
        this.globalData = globalData;
        this.tag = tag;
    }

    @Override
    public boolean check() throws QuestException {
        return globalData.hasTag(tag.getValue(null));
    }
}
