package org.betonquest.betonquest.item.handler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ExistenceArgument<T> extends Argument<Pair<Existence, @Nullable T>> {

    static <T> ExistenceArgument<T> fallback(@Nullable final ExistenceArgument<T> argument) {
        return argument == null ? whateverNullValue() : argument;
    }

    static <T> ExistenceArgument<List<T>> fallbackEmptyList(@Nullable final ExistenceArgument<List<T>> argument) {
        return argument == null ? whateverEmptyList() : argument;
    }

    static <T> ExistenceArgument<T> whateverNullValue() {
        return profile -> Pair.of(Existence.WHATEVER, null);
    }

    static <T> ExistenceArgument<T> whateverValue(final T value) {
        return profile -> Pair.of(Existence.WHATEVER, value);
    }

    static <T> ExistenceArgument<List<T>> whateverEmptyList() {
        return profile -> Pair.of(Existence.WHATEVER, List.of());
    }

    static <T> DecoratableChainRetriever<Pair<Existence, @Nullable T>> apply(
            final DecoratableChainRetriever<T> retriever) {
        return retriever
                .map(value -> Pair.of(Existence.REQUIRED, value))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, null));
    }

    static <T> ExistenceArgument<@Nullable T> apply(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return (ExistenceArgument<T>) apply(retriever)
                .get(key, Pair.of(Existence.WHATEVER, null));
    }

    @Nullable
    static <T> ExistenceArgument<@Nullable T> applyOrNull(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return (ExistenceArgument<T>) apply(retriever).get(key).orElse(null);
    }

    static <T> ExistenceArgument<@Nullable T> apply(
            final String key, final DecoratableChainRetriever<T> retriever, final T fallback) throws QuestException {
        return (ExistenceArgument<T>) apply(retriever)
                .get(key, Pair.of(Existence.WHATEVER, fallback));
    }

    /**
     * The list may be {@link Existence#NONE_KEY}.
     * <p>
     * The argument list must not be empty.
     */
    static <T> ExistenceArgument<List<T>> applyList(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return (ExistenceArgument<List<T>>) retriever
                .list().notEmpty()
                .map(list -> Pair.of(Existence.REQUIRED, list))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, null))
                .get(key, Pair.of(Existence.WHATEVER, List.of()));
    }

    @Nullable
    static <T> ExistenceArgument<List<T>> applyListOrNull(
            final String key, final DecoratableChainRetriever<T> retriever) throws QuestException {
        return (ExistenceArgument<List<T>>) retriever
                .list().notEmpty()
                .map(list -> Pair.of(Existence.REQUIRED, list))
                .prefilter(Existence.NONE_KEY, Pair.of(Existence.FORBIDDEN, null))
                .get(key).orElse(null);
    }
}
