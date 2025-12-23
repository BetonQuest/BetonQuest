package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A decorated {@link InstructionArgumentParser}.
 *
 * @param <T> the type of the argument
 */
public interface DecoratedArgumentParser<T> extends InstructionArgumentParser<T> {

    /**
     * Apply a {@link ValueValidator} to the {@link DecoratedArgumentParser} for early validation and improved error messages.
     *
     * @param validator the validator to apply to the argument
     * @return the new {@link DecoratedArgumentParser}
     */
    @Contract(value = "_ -> new", pure = true)
    DecoratedArgumentParser<T> validate(ValueValidator<T> validator);

    /**
     * Apply a {@link ValueValidator} to the argument for early validation and improved error messages.
     * The error message will be used if the validator fails and may contain a {@code %s} placeholder
     * for the wrong argument value according to {@link String#format(String, Object...)}.
     *
     * @param validator    the validator to apply to the argument
     * @param errorMessage the error message to use if the validator fails
     * @return the new {@link DecoratedArgumentParser}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<T> validate(ValueValidator<T> validator, String errorMessage);

    /**
     * Returns a new {@link DecoratedArgumentParser} that checks for the given expected string before
     * applying the {@link DecoratedArgumentParser} this method is called on.
     * If the expected string matches the {@link String} argument of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the current {@link DecoratedArgumentParser} instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new {@link DecoratedArgumentParser}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<T> prefilter(String expected, T fixedValue);

    /**
     * Returns a new {@link DecoratedArgumentParser} that checks for the given expected string before
     * applying the {@link DecoratedArgumentParser} this method is called on.
     * If the expected string matches the {@link String} argument of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the current {@link DecoratedArgumentParser} instance is called.
     * Since apply must not return null, this method returns an {@link Optional} of the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new {@link DecoratedArgumentParser}
     */
    @Contract(value = "_, _ -> new", pure = true)
    DecoratedArgumentParser<Optional<T>> prefilterOptional(String expected, @Nullable T fixedValue);
}
