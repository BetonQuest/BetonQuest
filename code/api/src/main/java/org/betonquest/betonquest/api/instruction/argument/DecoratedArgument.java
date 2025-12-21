package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A decorated {@link Argument}.
 *
 * @param <T> the type of the argument
 */
public interface DecoratedArgument<T> extends Argument<T> {

    /**
     * Apply a value checker to the argument for early validation and improved error messages.
     *
     * @param checker the checker to apply to the argument
     * @return the new argument
     */
    DecoratedArgument<T> validate(ValueValidator<T> checker);

    /**
     * Apply a value checker to the argument for early validation and improved error messages.
     * The error message will be used if the checker fails and may contain a {@code %s} placeholder for the wrong argument value
     * according to {@link String#format(String, Object...)}.
     *
     * @param checker      the checker to apply to the argument
     * @param errorMessage the error message to use if the checker fails
     * @return the new argument
     */
    DecoratedArgument<T> validate(ValueValidator<T> checker, String errorMessage);

    /**
     * Returns a new {@link Argument} that checks for the given expected string before
     * applying the {@link Argument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link Argument} instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new {@link Argument}
     */
    DecoratedArgument<T> prefilter(String expected, T fixedValue);

    /**
     * Returns a new {@link Argument} that checks for the given expected string before
     * applying the {@link Argument} this method is called on.
     * If the expected string matches the {@link String} argument of {@link Argument#apply(String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the {@link Argument#apply(String)} method of the current {@link Argument} instance is called.
     * Since Argument#apply(String) must not return null, this method returns an {@link Optional} of the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new {@link Argument}
     */
    DecoratedArgument<Optional<T>> prefilterOptional(String expected, @Nullable T fixedValue);
}
