package org.betonquest.betonquest.quest.action;

import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Static action that does nothing. It can be used as a placeholder when static action execution isn't an error in itself
 * or in the case that explicitly nothing should happen.
 */
public class DoNothingPlayerlessAction implements PlayerlessAction {

    /**
     * Create a static action placeholder that doesn't do anything.
     */
    public DoNothingPlayerlessAction() {
    }

    @Override
    public void execute() {
        // null object pattern
    }
}
