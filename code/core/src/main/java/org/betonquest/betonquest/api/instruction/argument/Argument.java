package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.VariableResolver;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Objectified parser for the Instruction to get a {@link T} from string.
 *
 * @param <T> what the argument returns
 */
@FunctionalInterface
public interface Argument<T> extends VariableResolver<T> {

    /**
     * Gets a {@link T} from string.
     *
     * @param string the string to parse
     * @return the {@link T}
     * @throws QuestException when the string cannot be parsed as {@link T}
     */
    @Override
    T apply(String string) throws QuestException;

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
    default Argument<T> prefilter(final String expected, final T fixedValue) {
        return string -> expected.equalsIgnoreCase(string) ? fixedValue : apply(string);
    }

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
    default Argument<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return string -> Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(string));
    }
}
