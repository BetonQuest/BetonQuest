package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * An extended {@link InstructionChainRetriever} offering additional methods
 * to modify the parsing process before retrieving the variable.
 *
 * @param <T> the type of the argument
 * @see InstructionChainRetriever
 */
public interface DecoratableChainRetriever<T> extends InstructionChainRetriever<T> {

    /**
     * Instead of reading a single value, parse the argument as a list of values.
     * Default implementation forwards to {@link #collect(Collector)} using {@link Collectors#toList()}.
     *
     * @return the new {@link DecoratableChainRetriever} with the new list of type T
     * @see #collect(Collector)
     * @see Collectors#toList()
     */
    @Contract(value = "-> new", pure = true)
    default DecoratableChainRetriever<List<T>> list() {
        return collect(Collectors.toList());
    }

    /**
     * Instead of reading a single value, parse the argument as a list of values
     * and collects them using the given collector.
     *
     * @param collector the collector to handle the list of values
     * @param <R>       the collected result type
     * @return the new {@link DecoratableChainRetriever} with the new collected type R
     */
    @Contract(value = "!null -> new", pure = true)
    <R> DecoratableChainRetriever<R> collect(Collector<T, ?, R> collector);

    /**
     * Map the value of the argument after parsing to another type.
     * Throwing an exception inside the mapper function indicates a parsing failure overall.
     *
     * @param mapper the mapper function to apply to the value after parsing
     * @param <U>    the new type of the mapped value
     * @return the new {@link DecoratableChainRetriever} with the new type
     * @see DecoratedArgumentParser#map(QuestFunction)
     */
    @Contract(value = "!null -> new", pure = true)
    <U> DecoratableChainRetriever<U> map(QuestFunction<T, U> mapper);

    /**
     * Apply a {@link ValueValidator} to the argument for early validation and improved error messages.
     * An error will be thrown if the validator fails (returns false).
     *
     * @param validator the validator to apply to the argument
     * @return the new validated {@link DecoratableChainRetriever}
     * @see DecoratedArgumentParser#validate(ValueValidator)
     * @see #validate(ValueValidator, String)
     */
    @Contract(value = "!null -> new", pure = true)
    DecoratableChainRetriever<T> validate(ValueValidator<T> validator);

    /**
     * Apply a {@link ValueValidator} to the argument for early validation and improved error messages.
     * The error message will be used if the validator fails (returns false) and may contain a {@code %s} placeholder
     * for the invalidated value to be included using {@link String#format(String, Object...)}.
     *
     * @param validator    the validator to apply to the argument
     * @param errorMessage the error message to use if the validator fails
     * @return the new validated {@link DecoratableChainRetriever}
     * @see DecoratedArgumentParser#validate(ValueValidator, String)
     * @see #validate(ValueValidator)
     */
    @Contract(value = "!null, !null -> new", pure = true)
    DecoratableChainRetriever<T> validate(ValueValidator<T> validator, String errorMessage);

    /**
     * Inverse of {@link #validate(ValueValidator)}.
     * The condition passed to {@link ValueValidator} determines an invalid case,
     * the error will be thrown if the validator passes (returns true).
     *
     * @param validator the validator to apply to the argument
     * @return the new validated {@link DecoratableChainRetriever}
     */
    @Contract(value = "!null -> new", pure = true)
    default DecoratableChainRetriever<T> invalidate(final ValueValidator<T> validator) {
        return validate(value -> !validator.validate(value));
    }

    /**
     * Inverse of {@link #validate(ValueValidator, String)}.
     * The condition passed to {@link ValueValidator} determines an invalid case,
     * the error will be thrown if the validator passes (returns true) and
     * its message may contain a {@code %s} placeholder for the invalidated value to be included
     * using {@link String#format(String, Object...)}.
     *
     * @param validator    the validator to apply to the argument
     * @param errorMessage the error message to use if the validator fails
     * @return the new validated {@link DecoratableChainRetriever}
     */
    @Contract(value = "!null, !null -> new", pure = true)
    default DecoratableChainRetriever<T> invalidate(final ValueValidator<T> validator, final String errorMessage) {
        return validate(value -> !validator.validate(value), errorMessage);
    }

    /**
     * Returns a new {@link DecoratableChainRetriever} that checks for the given expected string before
     * applying the parser this method is called on.
     * If the expected string matches the {@link String} argument of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the parser instance is called.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the non-null value to return if the expected string matches
     * @return the new prefiltered {@link DecoratableChainRetriever}
     * @see #prefilterOptional(String, Object)
     * @see DecoratedArgumentParser#prefilter(String, Object)
     */
    @Contract(value = "!null, !null -> new", pure = true)
    DecoratableChainRetriever<T> prefilter(String expected, T fixedValue);

    /**
     * Returns a new {@link DecoratableChainRetriever} that checks for the given expected string before
     * applying the parser this method is called on.
     * If the expected string matches the {@link String} argument of
     * {@link InstructionArgumentParser#apply(Variables, QuestPackageManager, QuestPackage, String)}
     * by {@link String#equalsIgnoreCase(String)}, the fixedValue is returned.
     * Otherwise, the apply method of the parser instance is called.
     * Since it must not return null, this method returns an {@link Optional} of the result.
     *
     * @param expected   the expected string to be matched
     * @param fixedValue the nullable value to return if the expected string matches
     * @return the new prefiltered {@link DecoratableChainRetriever}
     * @see #prefilter(String, Object)
     * @see DecoratedArgumentParser#prefilterOptional(String, Object)
     */
    @Contract(value = "!null, _ -> new", pure = true)
    DecoratableChainRetriever<Optional<T>> prefilterOptional(String expected, @Nullable T fixedValue);
}
