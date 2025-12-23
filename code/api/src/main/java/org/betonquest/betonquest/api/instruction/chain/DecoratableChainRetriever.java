package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * An extended {@link ChainRetriever} offering additional methods
 * to modify the parsing process before retrieving the variable.
 *
 * @param <T> the type of the variable
 * @see ChainRetriever
 */
public interface DecoratableChainRetriever<T> extends ChainRetriever<T> {

    @Override
    DecoratableChainRetriever<T> def(T defaultValue);

    /**
     * Apply a {@link ValueValidator} to the {@link DecoratedArgument} for early validation and improved error messages.
     *
     * @param validator the validator to apply to the argument
     * @return the new {@link DecoratedArgument}
     */
    @Contract(value = "_ -> new", pure = true)
    DecoratableChainRetriever<T> validate(ValueValidator<T> validator);

    /**
     * Apply a {@link ValueValidator} to the argument for early validation and improved error messages.
     * The error message will be used if the validator fails and may contain a {@code %s} placeholder
     * for the wrong argument value according to {@link String#format(String, Object...)}.
     *
     * @param validator    the validator to apply to the argument
     * @param errorMessage the error message to use if the validator fails
     * @return the new {@link DecoratedArgument}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratableChainRetriever<T> validate(ValueValidator<T> validator, String errorMessage);

    /**
     * Returns a new {@link DecoratedArgument} that checks for the given expected string before
     * applying the {@link DecoratedArgument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link DecoratedArgument} instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new {@link DecoratedArgument}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratableChainRetriever<T> prefilter(String expected, T fixedValue);

    /**
     * Returns a new {@link DecoratedArgument} that checks for the given expected string before
     * applying the {@link DecoratedArgument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link DecoratedArgument} instance is called.
     * Since {@link Argument#apply(String)} must not return null, this method returns an {@link Optional} of the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new {@link DecoratedArgument}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratableChainRetriever<Optional<T>> prefilterOptional(String expected, @Nullable T fixedValue);
}
