package org.betonquest.betonquest.quest.event.point;

/**
 * The type of point modification.
 */
public enum Point {

    /**
     * Adds the count to the current value.
     */
    ADD(Integer::sum),
    /**
     * Subtracts the count from the current value.
     */
    SET((current, count) -> count),
    /**
     * Multiplies the current value by the count.
     */
    MULTIPLY((current, count) -> current * count);

    /**
     * The calculator to use for this point type.
     */
    private final Calculator calculator;

    Point(final Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Calculates the new value.
     *
     * @param current the current value
     * @param count   the count
     * @return the new value
     */
    public int modify(final int current, final int count) {
        return calculator.calculate(current, count);
    }

    /**
     * The calculator interface.
     */
    private interface Calculator {
        /**
         * Calculates the new value.
         *
         * @param current the current value
         * @param count   the count
         * @return the new value
         */
        int calculate(int current, int count);
    }
}
