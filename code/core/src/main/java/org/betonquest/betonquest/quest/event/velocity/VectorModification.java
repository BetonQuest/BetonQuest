package org.betonquest.betonquest.quest.event.velocity;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Represents the type of modification how the given vector should get merged to the player-velocity.
 */
public enum VectorModification {
    /**
     * Sets the player-velocity to the given vector.
     */
    SET((player, vector) -> vector),
    /**
     * Adds the player-velocity to the given vector.
     */
    ADD((player, vector) -> vector.add(player.getVelocity()));

    /**
     * Instance of the calculator to calculate the vector.
     */
    private final Calculator calculator;

    VectorModification(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculate the new merged vector with the modification type.
     *
     * @param player the player
     * @param vector the vector
     * @return the merged vector
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
         * Applies a modification to the given vector.
         *
         * @param player the player to calculate the vector for
         * @param vector the vector to modify
         * @return the modified vector
         */
        Vector calculate(Player player, Vector vector);
    }
}
