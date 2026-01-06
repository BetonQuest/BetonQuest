package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.quest.action.PlayerlessAction;

/**
 * Static event that does nothing. It can be used as a placeholder when static event execution isn't an error in itself
 * or in the case that explicitly nothing should happen.
 */
public class DoNothingPlayerlessAction implements PlayerlessAction {

    /**
     * Create a static event placeholder that doesn't do anything.
     */
    public DoNothingPlayerlessAction() {
    }

    @Override
    public void execute() {
        // null object pattern
    }
}
