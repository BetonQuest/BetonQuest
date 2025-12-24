package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A decorated {@link InstructionArgumentParser} offering more methods to modify the result.
 *
 * @param <T> the type of the value
 */
public interface DecoratedArgumentParser<T> extends InstructionArgumentParser<T> {

    /**
     * Map the value of the argument <strong>after</strong> parsing the type.
     * Throwing an exception inside the mapper function indicates a parsing failure overall.
     *
     * @param mapper the mapper function to apply to the value after parsing
     * @param <U>    the new type of the mapped value
     * @return the new mapped {@link DecoratedArgumentParser} with potentially changed type
     */
    @Contract(value = "_ -> new", pure = true)
    <U> DecoratedArgumentParser<U> map(QuestFunction<T, U> mapper);

    /**
     * Apply a {@link ValueValidator} to the parser for early validation and improved error messages.
     * An error will be thrown if the validator fails (returns false).
     *
     * @param validator the validator to apply to the argument
     * @return the new validated {@link DecoratedArgumentParser}
     */
    @Contract(value = "_ -> new", pure = true)
    DecoratedArgumentParser<T> validate(ValueValidator<T> validator);

    /**
     * Apply a {@link ValueValidator} to the argument for early validation and improved error messages.
     * The error message will be used if the validator fails (returns false) and may contain a {@code %s} placeholder
     * for the wrong value to be included using {@link String#format(String, Object...)}.
     *
     * @param validator    the validator to apply to the argument
     * @param errorMessage the error message to use if the validator fails
     * @return the new validated {@link DecoratedArgumentParser}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<T> validate(ValueValidator<T> validator, String errorMessage);

    /**
     * Returns a new {@link DecoratedArgumentParser} that checks for the given expected string before
     * applying the parser this method is called on.
     * If the expected string matches the string parameter of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the current {@link DecoratedArgumentParser} instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new prefiltered {@link DecoratedArgumentParser}
     * @see #prefilterOptional(String, Object)
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<T> prefilter(String expected, T fixedValue);

    /**
     * Returns a new {@link DecoratedArgumentParser} that checks for the given expected string before
     * applying the parser this method is called on.
     * If the expected string matches the string parameter of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the current parser instance is called.
     * Since apply must not return null, this method returns an {@link Optional} wrapping the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new prefiltered {@link DecoratedArgumentParser}
     * @see #prefilter(String, Object)
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<Optional<T>> prefilterOptional(String expected, @Nullable T fixedValue);
}
