package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Empty action that does throw an exception when executed.
 * It can be used as a placeholder when playerless action execution is not defined.
 */
public class ThrowExceptionPlayerlessAction implements PlayerlessAction {

    /**
     * Additional warning text.
     */
    private final String warning;

    /**
     * Create a playerless action placeholder that throws an exception when executed.
     *
     * @param warning additional warning text
     */
    public ThrowExceptionPlayerlessAction(final String warning) {
        this.warning = warning;
    }

    @Override
    public void execute() throws QuestException {
        throw new QuestException("Executing in an independent context failed: %s".formatted(warning));
    }
}
