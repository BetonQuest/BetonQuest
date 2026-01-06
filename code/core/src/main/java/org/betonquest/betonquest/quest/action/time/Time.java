package org.betonquest.betonquest.quest.action.time;

import org.bukkit.World;

/**
 * Represents the time that can be applied to the world.
 */
public enum Time {

    /**
     * Sets the time of the world.
     */
    SET((world, time) -> time + 18_000),
    /**
     * Adds the given time to the current time of the world.
     */
    ADD((world, time) -> (world.getTime() + time) % 24_000),
    /**
     * Subtracts the given time from the current time of the world.
     */
    SUBTRACT((world, time) -> (world.getTime() - time) % 24_000);

    /**
     * Instance of the calculator to calculate the time.
     */
    private final TimeCalculator timeCalculator;

    Time(final TimeCalculator timeCalculator) {
        this.timeCalculator = timeCalculator;
    }

    /**
     * Gets the time for the given prefix.
     * <ul>
     *     <li>{@code +} - {@link #ADD}</li>
     *     <li>{@code -} - {@link #SUBTRACT}</li>
     * </ul>
     *
     * @param prefix the character identifying the {@link Time}
     * @return the {@link Time} according to the prefix or {@link #SET} if no match was found
     */
    public static Time getForPrefix(final char prefix) {
        return switch (prefix) {
            case '+' -> ADD;
            case '-' -> SUBTRACT;
            default -> SET;
        };
    }

    /**
     * Applies the time to the world.
     *
     * @param world the world to apply the time to
     * @param time  the time to apply to the world
     * @return the calculated time
     */
    public long applyTo(final World world, final long time) {
        return timeCalculator.calculate(world, time);
    }

    /**
     * Functional interface to calculate the time.
     */
    @FunctionalInterface
    private interface TimeCalculator {

        /**
         * Calculates the time to apply to the world.
         *
         * @param world the world to calculate the time for
         * @param time  the time to apply to the world
         * @return the calculated time
         */
        long calculate(World world, long time);
    }
}
