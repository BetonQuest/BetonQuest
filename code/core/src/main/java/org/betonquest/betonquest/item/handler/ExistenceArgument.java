package org.betonquest.betonquest.item.handler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.betonquest.betonquest.api.instruction.chain.ListChainRetriever;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An Argument of a Pair of Existence and the actual wanted value.
 *
 * @param <T> the value of the argument
 */
@FunctionalInterface
public interface ExistenceArgument<T> extends Argument<Pair<Existence, @Nullable T>> {

    /**
     * Checks if the argument exists and uses {@link #whateverNullValue()} as fallback if null.
     *
     * @param argument the argument to eventually get the fallback for
     * @param <T>      the value type
     * @return the argument if not null, otherwise {@link #whateverNullValue()}
     */
    static <T> ExistenceArgument<T> fallback(@Nullable final ExistenceArgument<T> argument) {
        return argument == null ? whateverNullValue() : argument;
    }

    /**
     * Checks if the argument exists and uses {@link #whateverEmptyList()} as fallback if null.
     *
     * @param argument the argument to eventually get the fallback for
     * @param <T>      the value type
     * @return the argument if not null, otherwise {@link #whateverEmptyList()}
     */
    static <T> ExistenceArgument<List<T>> fallbackEmptyList(@Nullable final ExistenceArgument<List<T>> argument) {
        return argument == null ? whateverEmptyList() : argument;
    }

    /**
     * An argument without explicit values.
     * <p>
     * It is {@link Existence#WHATEVER} with {@code null} value.
     *
     * @param <T> the value type
     * @return the default argument without value
     */
    static <T> ExistenceArgument<T> whateverNullValue() {
        return profile -> Pair.of(Existence.WHATEVER, null);
    }

    /**
     * An argument with an explicit value.
     * <p>
     * It is {@link Existence#WHATEVER} with the given value.
     *
     * @param value the value to use
     * @param <T>   the value type
     * @return the default argument with value
     */
    static <T> ExistenceArgument<T> whateverValue(final T value) {
        return profile -> Pair.of(Existence.WHATEVER, value);
    }

    /**
     * An argument without explicit values.
     * <p>
     * It is {@link Existence#WHATEVER} with {@link List#of()} as value.
     *
     * @param <T> the value type
     * @return the default argument without explicit values
     */
    static <T> ExistenceArgument<List<T>> whateverEmptyList() {
        return profile -> Pair.of(Existence.WHATEVER, List.of());
    }

    /**
     * Setup a retriever to parses a value.
     * <p>
     * If the value is parsed it will be paired with {@link Existence#REQUIRED} and returned.
     * <p>
     * If the raw value is {@link Existence#NONE_KEY} a {@code null} value with {@link Existence#FORBIDDEN}
     * will be returned instead.
     *
     * @param retriever the retriever for the value
     * @param <T>       the value type
     * @return the existence with the value, or null if the key is not present
     */
    static <T> DecoratableChainRetriever<Pair<Existence, @Nullable T>> apply(
            final DecoratableChainRetriever<T> retriever) {
        return retriever
                .map(value -> Pair.of(Existence.REQUIRED, value))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, null));
    }

    /**
     * Parses a value from a key with {@link #whateverNullValue()} as fallback.
     * <p>
     * If the key is present the value with {@link Existence#REQUIRED} will be returned.
     * <p>
     * If the raw value is {@link Existence#NONE_KEY} a {@code null} value with {@link Existence#FORBIDDEN}
     * will be returned instead.
     * <p>
     * If the key is not present, effectively {@link #whateverNullValue()} will be returned.
     *
     * @param key       the instruction key
     * @param retriever the retriever for the value
     * @param <T>       the value type
     * @return the existence with the value, or null if the key is not present
     * @throws QuestException if the pre-validation of the value fails
     */
    static <T> ExistenceArgument<@Nullable T> apply(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return apply(retriever).get(key, Pair.of(Existence.WHATEVER, null))::getValue;
    }

    /**
     * Parses a value from a key.
     * <p>
     * If the key is present the value with {@link Existence#REQUIRED} will be returned.
     * <p>
     * If the raw value is {@link Existence#NONE_KEY} a {@code null} value with {@link Existence#FORBIDDEN}
     * will be returned instead.
     * <p>
     * If the key is not present {@code null} will be returned.
     *
     * @param key       the instruction key
     * @param retriever the retriever for the value
     * @param <T>       the value type
     * @return the existence with the value, or null if the key is not present
     * @throws QuestException if the pre-validation of the value fails
     */
    @Nullable
    static <T> ExistenceArgument<@Nullable T> applyOrNull(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return apply(retriever)
                .get(key)
                .map(argument -> (ExistenceArgument<T>) argument::getValue)
                .orElse(null);
    }

    /**
     * Parses a non-empty list of values from a key.
     * If the key is present a list with {@link Existence#REQUIRED} will be returned.
     * <p>
     * The list will be checked with {@link ListChainRetriever#notEmpty()}.
     * <p>
     * If the raw value is {@link Existence#NONE_KEY} an empty list with {@link Existence#FORBIDDEN}
     * will be returned instead.
     *
     * @param key       the instruction key
     * @param retriever the retriever for the value list
     * @param <T>       the value type
     * @return the existence with the list of values, or null if the key is not present
     * @throws QuestException if the pre-validation of the value list fails
     */
    @Nullable
    static <T> ExistenceArgument<List<T>> applyListOrNull(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return retriever
                .list().notEmpty()
                .map(list -> Pair.of(Existence.REQUIRED, list))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, List.of()))
                .get(key)
                .map(argument -> (ExistenceArgument<List<T>>) argument::getValue)
                .orElse(null);
    }
}
