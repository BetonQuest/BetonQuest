package org.betonquest.betonquest.quest.variable.point;

/**
 * An abstract class for creating Point variables.
 *
 * @param <T> the data holder type
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractPointVariable<T> {

    /**
     * The data holder.
     */
    protected final T data;

    /**
     * The category of the point.
     */
    protected final String category;

    /**
     * The number to calculate the point to.
     */
    protected final int amount;

    /**
     * The type of how the points should be calculated.
     */
    protected final PointCalculationType type;

    /**
     * Creates a new PointVariable.
     *
     * @param data     the data holder
     * @param category the category of the point
     * @param amount   the number to calculate the point to
     * @param type     the type of how the points should be calculated
     */
    public AbstractPointVariable(final T data, final String category, final int amount, final PointCalculationType type) {
        this.data = data;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    /**
     * Get the value for the given amount.
     *
     * @param amount the amount to get the value for
     * @return the value for the given amount
     */
    @SuppressWarnings("PMD.TooFewBranchesForSwitch")
    public String getValueFor(final int amount) {
        return switch (type) {
            case AMOUNT -> String.valueOf(amount);
            case LEFT -> String.valueOf(amount - this.amount);
        };
    }
}
