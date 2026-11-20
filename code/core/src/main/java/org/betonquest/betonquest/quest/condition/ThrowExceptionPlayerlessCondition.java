package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;

/**
 * A playerless condition placeholder that throws an exception when checked.
 */
public class ThrowExceptionPlayerlessCondition implements PlayerlessCondition {

    /**
     * Additional warning text.
     */
    private final String warning;

    /**
     * Create a playerless condition that throws an exception when checked.
     *
     * @param warning additional warning text
     */
    public ThrowExceptionPlayerlessCondition(final String warning) {
        this.warning = warning;
    }

    @Override
    public boolean check() throws QuestException {
        throw new QuestException("Executing in an independent context failed: %s".formatted(warning));
    }
}
