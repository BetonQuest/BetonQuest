package org.betonquest.betonquest.quest.event.lever;

/**
 * Represents the type of State, the lever should get set to.
 */
public enum StateType {
    /**
     * Sets the lever to on.
     */
    @SuppressWarnings("PMD.ShortVariable")
    ON(power -> true),
    /**
     * Sets the lever to off.
     */
    OFF(power -> false),
    /**
     * Toggles the lever.
     */
    TOGGLE(power -> !power);

    /**
     * The calculator to use for this state type.
     */
    private final Calculator calculator;

    StateType(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculates the new power state of the lever.
     *
     * @param power the current power state of the lever
     * @return the new power state of the lever
     */
    public boolean apply(final boolean power) {
        return calculator.calculate(power);
    }

    /**
     * Functional interface to calculate the new power state of the lever.
     */
    @FunctionalInterface
    private interface Calculator {
        /**
         * Calculates the new power state of the lever.
         *
         * @param power the current power state of the lever
         * @return the new power state of the lever
         */
        boolean calculate(boolean power);
    }
}
