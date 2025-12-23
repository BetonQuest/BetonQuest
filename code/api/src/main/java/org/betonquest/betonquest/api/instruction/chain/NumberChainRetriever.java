package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedNumberArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An extended {@link InstructionChainRetriever} offering additional methods
 * to modify the parsing process before retrieving the variable explicitly for the {@link Number} type.
 *
 * @see InstructionChainRetriever
 * @see DecoratableChainRetriever
 */
public interface NumberChainRetriever extends DecoratableChainRetriever<Number> {

    @Override
    NumberChainRetriever prefilter(String expected, Number fixedValue);

    @Override
    DecoratableChainRetriever<Optional<Number>> prefilterOptional(String expected, @Nullable Number fixedValue);

    @Override
    NumberChainRetriever validate(ValueValidator<Number> validator);

    @Override
    NumberChainRetriever validate(ValueValidator<Number> validator, String errorMessage);

    @Override
    NumberChainRetriever def(Number defaultValue);

    /**
     * Adds a lower bound to the {@link DecoratedNumberArgument}.
     * This method should use validation to ensure the value to be above the given inclusive minimum.
     *
     * @param inclusiveMin the inclusive minimum to check against
     * @return a new {@link DecoratedNumberArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    NumberChainRetriever atLeast(Number inclusiveMin);

    /**
     * Adds an upper bound to the {@link DecoratedNumberArgument}.
     * This method should use validation to ensure the value to be below the given inclusive maximum.
     *
     * @param inclusiveMax the inclusive maximum to check against
     * @return a new {@link DecoratedNumberArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    NumberChainRetriever atMost(Number inclusiveMax);

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
    NumberChainRetriever inRange(Number inclusiveMin, Number exclusiveMax);
}
