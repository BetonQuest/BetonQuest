package org.betonquest.betonquest.quest.event.point;

/**
 * The type of point modification.
 */
public enum PointType {

    /**
     * Adds the count to the current value.
     */
    ADD((current, count) -> current + (int) count, "point_given"),
    /**
     * Subtracts the count from the current value.
     */
    SUBTRACT((current, count) -> current - (int) count, "point_taken"),
    /**
     * Sets the current value to the count.
     */
    SET((current, count) -> (int) count, "point_set"),
    /**
     * Multiplies the current value by the count.
     */
    MULTIPLY((current, count) -> (int) (current * count), "point_multiplied");

    /**
     * The calculator to use for this point type.
     */
    private final Calculator calculator;

    /**
     * The name of the category of the modification.
     */
    private final String notifyCategory;

    PointType(final Calculator calculator, final String notifyCategory) {
        this.calculator = calculator;
        this.notifyCategory = notifyCategory;
    }

    /**
     * Calculates the new value.
     *
     * @param current the current value
     * @param count   the count
     * @return the new value
     */
    public int modify(final int current, final double count) {
        return calculator.calculate(current, count);
    }

    /**
     * Returns the name of the category of the modification.
     *
     * @return the name of the category of the modification
     */
    public String getNotifyCategory() {
        return notifyCategory;
    }

    /**
     * The calculator interface.
     */
    @FunctionalInterface
    private interface Calculator {

        /**
         * Calculates the new value.
         *
         * @param current the current value
         * @param count   the count
         * @return the new value
         */
        int calculate(int current, double count);
    }
}
