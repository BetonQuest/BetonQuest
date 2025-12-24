package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.jetbrains.annotations.Contract;

/**
 * A decorated {@link Number} argument offering more options on top of the {@link DecoratedArgumentParser} itself.
 */
public interface NumberArgumentParser extends DecoratedArgumentParser<Number> {

    /**
     * Adds a lower bound to the {@link NumberArgumentParser}.
     * This method uses {@link DecoratedArgumentParser#validate(ValueValidator)}
     * to ensure the value to be above the given inclusive minimum.
     *
     * @param inclusiveMin the inclusive minimum to check against
     * @return a new {@link NumberArgumentParser}
     */
    @Contract(value = "_ -> new", pure = true)
    NumberArgumentParser atLeast(Number inclusiveMin);

    /**
     * Adds an upper bound to the {@link NumberArgumentParser}.
     * This method uses {@link DecoratedArgumentParser#validate(ValueValidator)}
     * to ensure the value to be below the given inclusive maximum.
     *
     * @param inclusiveMax the inclusive maximum to check against
     * @return a new {@link NumberArgumentParser}
     */
    @Contract(value = "_ -> new", pure = true)
    NumberArgumentParser atMost(Number inclusiveMax);

    /**
     * Adds both a lower and upper bound to the {@link NumberArgumentParser}.
     * This method should use {@link DecoratedArgumentParser#validate(ValueValidator)} to ensure the value is between
     * the given inclusive minimum and exclusive maximum.
     *
     * @param inclusiveMin the inclusive minimum to check against
     * @param exclusiveMax the exclusive maximum to check against
     * @return a new {@link NumberArgumentParser}
     */
    @Contract(value = "_, _ -> new", pure = true)
    NumberArgumentParser inRange(Number inclusiveMin, Number exclusiveMax);
}
