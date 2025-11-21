package org.betonquest.betonquest.quest.objective.npc;

/**
 * The action that completes the objective.
 */
public enum Trigger {
    /**
     * The player has to enter the range.
     * <p>
     * When the player is already inside the range he has to leave first.
     */
    ENTER,
    /**
     * The player has to leave the range.
     * <p>
     * If the player is already outside the range he has to enter first.
     */
    LEAVE,
    /**
     * The player has to be inside the range.
     */
    INSIDE,
    /**
     * The player has to be outside the range.
     */
    OUTSIDE
}
