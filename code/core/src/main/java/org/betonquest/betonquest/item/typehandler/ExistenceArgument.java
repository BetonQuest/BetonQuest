package org.betonquest.betonquest.item.typehandler;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.chain.DecoratableChainRetriever;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExistenceArgument<T> extends Argument<Pair<Existence, @Nullable T>> {

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
}
