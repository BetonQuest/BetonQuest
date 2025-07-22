package org.betonquest.betonquest.quest.condition.facing;

/**
 * Represents a direction the player can face.
 */
public enum Direction {
    /**
     * The player is facing north.
     */
    NORTH,
    /**
     * The player is facing east.
     */
    EAST,
    /**
     * The player is facing south.
     */
    SOUTH,
    /**
     * The player is facing west.
     */
    WEST,
    /**
     * The player is facing up.
     */
    @SuppressWarnings("PMD.ShortVariable")
    UP,
    /**
     * The player is facing down.
     */
    DOWN;

    /**
     * Parses the direction based on the player's rotation and pitch.
     *
     * @param yawn  The player's yawn/rotation
     * @param pitch The player's pitch
     * @return The direction the player is facing
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public static Direction parseDirection(final float yawn, final float pitch) {
        if (pitch > 60) {
            return DOWN;
        } else if (pitch < -60) {
            return UP;
        } else {
            final float rotation = (yawn + 360) % 360;
            if (rotation < 45 || rotation >= 315) {
                return SOUTH;
            } else if (rotation < 135) {
                return WEST;
            } else if (rotation < 225) {
                return NORTH;
            } else {
                return EAST;
            }
        }
    }
}
