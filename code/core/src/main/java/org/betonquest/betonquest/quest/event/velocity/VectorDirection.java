package org.betonquest.betonquest.quest.event.velocity;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Represents the type of direction how the given vector should get rotated.
 */
public enum VectorDirection {
    /**
     * The vector is absolute and won't get rotated.
     */
    ABSOLUTE((player, vector) -> vector),
    /**
     * The vector is relative to the line of sight and will get rotated to match it.
     */
    RELATIVE((player, vector) -> {
        final Location playerLoc = player.getLocation();
        vector.rotateAroundY(-Math.toRadians(playerLoc.getYaw()));
        final Vector vec = new Vector(0, 0, 1).rotateAroundY(-Math.toRadians(playerLoc.getYaw() + 90));
        vector.rotateAroundAxis(vec, -Math.toRadians(playerLoc.getPitch()));
        return vector;
    }),
    /**
     * The vector is relative to the line of sight but only horizontal. The vertical (Y) velocity is absolute.
     */
    RELATIVE_Y((player, vector) -> {
        final Location playerLoc = player.getLocation();
        vector.rotateAroundY(-Math.toRadians(playerLoc.getYaw()));
        return vector;
    });

    /**
     * Instance of the calculator to calculate the vector.
     */
    private final Calculator calculator;

    VectorDirection(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculate the rotation of the vector. Eventual depends on the players view
     *
     * @param player the player
     * @param vector the vector
     * @return the rotated vector
     */
    public Vector calculate(final Player player, final Vector vector) {
        return calculator.calculate(player, vector.clone());
    }

    /**
     * Functional interface to calculate the vector.
     */
    @FunctionalInterface
    private interface Calculator {
        /**
         * Calculate the rotation of the vector. Eventual depends on the players view
         *
         * @param player the player to rotate the vector for
         * @param vector the vector to rotate
         * @return the rotated vector
         */
        Vector calculate(Player player, Vector vector);
    }
}
