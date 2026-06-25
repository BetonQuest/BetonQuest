package org.betonquest.betonquest.lib.argument.type;

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
    SECONDS(TICKS, time -> time * 20),

    /**
     * The time in ticks from minutes.
     */
    MINUTES(SECONDS, time -> time * 60),

    /**
     * The time in ticks from hours.
     */
    HOURS(MINUTES, time -> time * 60),

    /**
     * The time in ticks from days.
     */
    DAYS(HOURS, time -> time * 24),

    /**
     * The time in ticks from weeks.
     */
    WEEKS(DAYS, time -> time * 7),

    /**
     * The time in ticks from months.
     */
    MONTHS(DAYS, time -> time * 30),

    /**
     * The time in ticks from years.
     */
    YEARS(DAYS, time -> time * 365);

    /**
     * Instance of the calculator to calculate the ticks.
     */
    private final Calculator calculator;

    /**
     * Creates a new TimeUnit.
     *
     * @param calculator the calculator to calculate the ticks
     */
    TimeUnit(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Creates a new TimeUnit.
     *
     * @param source     the source TimeUnit
     * @param calculator the calculator to calculate the ticks from the source unit
     */
    TimeUnit(final TimeUnit source, final Calculator calculator) {
        this.calculator = time -> calculator.calculate(source.getTicks(time));
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
