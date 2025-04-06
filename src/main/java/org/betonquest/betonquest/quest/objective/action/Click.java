package org.betonquest.betonquest.quest.objective.action;

import org.bukkit.event.block.Action;

/**
 * Enum representing different types of click actions.
 */
public enum Click {
    /**
     * Right click action.
     */
    RIGHT,
    /**
     * Left click action.
     */
    LEFT,
    /**
     * Any click action (left or right).
     */
    ANY;

    /**
     * Checks if the action matches the current click type.
     *
     * @param action The action to check.
     * @return True if the action matches, false otherwise.
     */
    public boolean match(final Action action) {
        if (action == Action.PHYSICAL) {
            return false;
        }
        return this == ANY || this == RIGHT && action.isRightClick() || this == LEFT && action.isLeftClick();
    }
}
