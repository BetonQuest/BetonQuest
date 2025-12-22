package org.betonquest.betonquest.api.instruction.argument;

import org.jetbrains.annotations.Contract;

/**
 * A decorated {@link Number} argument offering more options on top of the {@link DecoratedArgument} itself.
 */
public interface DecoratedNumberArgument extends DecoratedArgument<Number> {

    /**
     * Adds a lower bound to the {@link DecoratedNumberArgument}.
     * This method should use validation to ensure the value to be above the given inclusive minimum.
     *
     * @param inclusiveMin the inclusive minimum to check against
     * @return a new {@link DecoratedNumberArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    DecoratedNumberArgument atLeast(Number inclusiveMin);

    /**
     * Adds an upper bound to the {@link DecoratedNumberArgument}.
     * This method should use validation to ensure the value to be below the given inclusive maximum.
     *
     * @param inclusiveMax the inclusive maximum to check against
     * @return a new {@link DecoratedNumberArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    DecoratedNumberArgument atMost(Number inclusiveMax);

    /**
     * Adds both a lower and upper bound to the {@link DecoratedNumberArgument}.
     * This method should use validation to ensure the value is between
     * the given inclusive minimum and exclusive maximum.
     *
     * @param inclusiveMin the inclusive minimum to check against
     * @param exclusiveMax the exclusive maximum to check against
     * @return a new {@link DecoratedNumberArgument}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedNumberArgument bounds(Number inclusiveMin, Number exclusiveMax);
}
