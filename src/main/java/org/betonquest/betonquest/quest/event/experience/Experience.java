package org.betonquest.betonquest.quest.event.experience;

import org.bukkit.entity.Player;

/**
 * Represents the type of modification how the given amount should get applied to the player-experience
 */
public enum Experience {
    /**
     * Adds the given amount to the player-experience, just experience points
     */
    ADD_EXPERIENCE((player, amount) -> player.giveExp((int) amount)),
    /**
     * Adds the given amount to the player-experience, levels and or percentage to the next level
     */
    ADD_LEVEL((player, amount) -> {
        if (amount % 1 == 0) {
            player.giveExpLevels((int) amount);
        } else {
            final double current = player.getLevel() + player.getExp();
            final double amountToAdd = current + amount;
            player.setLevel((int) amountToAdd);
            player.setExp((float) (amountToAdd - (int) amountToAdd));
        }
    }),
    /**
     * Sets the player-experience to the next level to the given amount
     */
    SET_EXPERIENCE_BAR(Player::setExp),
    /**
     * Sets the player-experience-level to the given amount
     */
    SET_LEVEL((player, amount) -> {
        player.setLevel((int) amount);
        if (amount % 1 != 0) {
            player.setExp((float) (amount - (int) amount));
        }
    });

    /**
     * Instance of the calculator to calculate the experience
     */
    private final Calculator calculator;

    Experience(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculate the new experience with the modification type
     *
     * @param player the player
     * @param amount the amount
     */
    public void applyExperience(final Player player, final float amount) {
        calculator.calculate(player, amount);
    }

    /**
     * Functional interface to calculate the experience
     */
    private interface Calculator {
        /**
         * Calculates the amount to apply to the player
         *
         * @param player the player to apply the amount to
         * @param amount the amount to apply
         * @return the calculated amount
         */
        void calculate(Player player, float amount);
    }

}
