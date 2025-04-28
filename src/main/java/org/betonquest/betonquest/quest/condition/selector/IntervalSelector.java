package org.betonquest.betonquest.quest.condition.selector;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;

import javax.annotation.Nullable;

/**
 * Selector to check if a value is within a certain interval.
 */
public class IntervalSelector implements Selector {

    /**
     * The {@link Variable<Number>} to check the value against.
     */
    private final Variable<Number> first;

    /**
     * The second number to compare with.
     */
    private final Variable<Number> second;

    /**
     * The bounds of the interval.
     */
    private final IntervalBounds intervalBounds;

    /**
     * Creates a new IntervalSelector.
     *
     * @param first          the first number
     * @param second         the second number
     * @param intervalBounds the bounds of the interval
     */
    public IntervalSelector(final Variable<Number> first, final Variable<Number> second, final IntervalBounds intervalBounds) {
        this.first = first;
        this.second = second;
        this.intervalBounds = intervalBounds;
    }

    @Override
    public boolean matches(@Nullable final Profile profile, final Number value) throws QuestException {
        final double firstValue = first.getValue(profile).doubleValue();
        final double secondValue = second.getValue(profile).doubleValue();
        return intervalBounds.check(value.doubleValue(), Math.min(firstValue, secondValue), Math.max(firstValue, secondValue));
    }

    /**
     * The methode for ex-/including the bounds of the interval.
     */
    public enum IntervalBounds {
        /**
         * The interval is exclusive on both sides.
         */
        EXCLUSIVE((value, lower, upper) -> value > lower && value < upper),//><
        /**
         * The interval is exclusive on the lower side and inclusive on the upper side.
         */
        LOWER_EXCLUSIVE((value, lower, upper) -> value > lower && value <= upper),//><=
        /**
         * The interval is inclusive on the lower side and exclusive on the upper side.
         */
        HIGHER_EXCLUSIVE((value, lower, upper) -> value >= lower && value < upper),//<=<
        /**
         * The interval is inclusive on both sides.
         */
        INCLUSIVE((value, lower, upper) -> value >= lower && value <= upper);//<=>

        /**
         * The compare to use for this operation.
         */
        private final Compare compare;

        IntervalBounds(final Compare compare) {
            this.compare = compare;
        }

        /**
         * Checks if the value is in the interval.
         *
         * @param value number to compare
         * @param lower number to compare
         * @param upper number to compare
         * @return true if the value is in the interval
         */
        public boolean check(final double value, final double lower, final double upper) {
            return compare.check(value, lower, upper);
        }

        /**
         * The compare interface.
         */
        @FunctionalInterface
        private interface Compare {
            /**
             * Compares if the first number is in the interval of the second and third number.
             *
             * @param value number to compare
             * @param lower number to compare
             * @param upper number to compare
             * @return the result of the compare
             */
            boolean check(double value, double lower, double upper);
        }
    }
}
