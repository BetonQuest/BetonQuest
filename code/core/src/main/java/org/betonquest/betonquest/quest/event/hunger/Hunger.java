package org.betonquest.betonquest.quest.event.hunger;

import org.bukkit.entity.Player;

/**
 * Represents the type of modification how the given amount should get applied to the player-hunger.
 */
public enum Hunger {
    /**
     * Adds the given amount to the player-hunger up to 20.
     */
    GIVE((player, amount) -> Math.min(player.getFoodLevel() + amount, 20)),
    /**
     * Removes the given amount from the player-hunger down to 0.
     */
    TAKE((player, amount) -> Math.max(player.getFoodLevel() - amount, 0)),
    /**
     * Sets the player-hunger to the given amount.
     */
    SET((player, amount) -> amount);

    /**
     * Instance of the calculator to calculate the hunger.
     */
    private final Calculator calculator;

    Hunger(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculate the new hunger with the modification type.
     *
     * @param player the player
     * @param amount the amount
     * @return the new hunger
     */
    public int calculate(final Player player, final int amount) {
        return calculator.calculate(player, amount);
    }

    /**
     * Functional interface to calculate the hunger.
     */
    @FunctionalInterface
    private interface Calculator {
        /**
         * Calculates the amount to apply to the player.
         *
         * @param player the player to apply the amount to
         * @param amount the amount to apply
         * @return the calculated amount
         */
        int calculate(Player player, int amount);
    }
}
