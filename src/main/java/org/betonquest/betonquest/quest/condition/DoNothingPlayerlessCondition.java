package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

/**
 * A playerless condition placeholder that is always false.
 */
public class DoNothingPlayerlessCondition implements PlayerlessCondition {

    /**
     * Create a playerless condition that does nothing and is always false.
     */
    public DoNothingPlayerlessCondition() {
    }

    @Override
    public boolean check() {
        return false;
    }
}
