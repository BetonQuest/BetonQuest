package org.betonquest.betonquest.quest.event.folder;

/**
 * Represents the time in ticks.
 */
public enum TimeUnit {
    /**
     * The time in ticks from ticks.
     */
    TICKS(time -> time),
    /**
     * The time in ticks from seconds.
     */
    SECONDS(time -> time * 20),
    /**
     * The time in ticks from minutes.
     */
    MINUTES(time -> time * 20 * 60);

    /**
     * Instance of the calculator to calculate the ticks.
     */
    private final Calculator calculator;

    TimeUnit(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculate the time in ticks.
     *
     * @param time the time
     * @return the time in ticks
     */
    public long getTicks(final long time) {
        return calculator.calculate(time);
    }

    /**
     * Functional interface to calculate the time in ticks.
     */
    @FunctionalInterface
    private interface Calculator {

        /**
         * Applies a modification to the given time.
         *
         * @param time the time to calculate the ticks for
         * @return the calculated ticks
         */
        long calculate(long time);
    }
}
